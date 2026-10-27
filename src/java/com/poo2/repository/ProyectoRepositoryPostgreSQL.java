
package com.poo2.repository;

import com.poo2.modelo.Proyecto;
import com.poo2.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProyectoRepositoryPostgreSQL implements ProyectoRepository{
    
    // Registrar un proyecto nuevo.
    @Override
    public boolean registrar(Proyecto proyecto) {

        String sql = """
            INSERT INTO proyectos
                (nombre, descripcion, tecnologia, estado, id_usuario)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id_proyecto, fecha_creacion, fecha_actualizacion
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setString(3, proyecto.getTecnologia());
            ps.setString(4, "REGISTRADO");
            ps.setInt(5, proyecto.getIdUsuario());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proyecto.setIdProyecto(
                            rs.getInt("id_proyecto"));

                    proyecto.setEstado("REGISTRADO");

                    proyecto.setFechaCreacion(
                            convertirFecha(
                                    rs.getTimestamp("fecha_creacion")));

                    proyecto.setFechaActualizacion(
                            convertirFecha(
                                    rs.getTimestamp("fecha_actualizacion")));

                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al registrar el proyecto.", e);
        }
    }

    // Listar los proyectos de un usuario específico.
    @Override
    public List<Proyecto> listarPorUsuario(int idUsuario) {

        String sql = """
            SELECT *
            FROM proyectos
            WHERE id_usuario = ?
            ORDER BY fecha_creacion DESC
            """;

        List<Proyecto> proyectos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    proyectos.add(mapearProyecto(rs));
                }
            }

            return proyectos;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar los proyectos del usuario.", e);
        }
    }

    // Listar todos los proyectos del sistema.
    @Override
    public List<Proyecto> listarTodos() {

        String sql = """
            SELECT *
            FROM proyectos
            ORDER BY fecha_creacion DESC
            """;

        List<Proyecto> proyectos = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                proyectos.add(mapearProyecto(rs));
            }

            return proyectos;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar todos los proyectos.", e);
        }
    }

    // Buscar un proyecto por su identificador.
    @Override
    public Proyecto buscarPorId(int idProyecto) {

        String sql = """
            SELECT *
            FROM proyectos
            WHERE id_proyecto = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProyecto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProyecto(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el proyecto.", e);
        }
    }

    // Actualizar los datos de un proyecto.
    @Override
    public boolean actualizar(Proyecto proyecto) {

        String sql = """
            UPDATE proyectos
            SET nombre = ?,
                descripcion = ?,
                tecnologia = ?,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id_proyecto = ?
              AND id_usuario = ?
              AND estado <> 'INACTIVO'
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setString(3, proyecto.getTecnologia());
            ps.setInt(4, proyecto.getIdProyecto());
            ps.setInt(5, proyecto.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el proyecto.", e);
        }
    }
    
    // Actualizar cualquier proyecto como administrador.
    @Override
    public boolean actualizarComoAdmin(Proyecto proyecto) {

        String sql = """
            UPDATE proyectos
            SET nombre = ?,
                descripcion = ?,
                tecnologia = ?,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id_proyecto = ?
              AND estado <> 'INACTIVO'
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setString(2, proyecto.getDescripcion());
            ps.setString(3, proyecto.getTecnologia());
            ps.setInt(4, proyecto.getIdProyecto());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el proyecto como administrador.", e);
        }
    }

    // Cambiar el estado de un proyecto.
    @Override
    public boolean cambiarEstado(int idProyecto, String estado) {

        if (estado == null
                || !estado.matches(
                        "REGISTRADO|EN_EVALUACION|EVALUADO|INACTIVO")) {
            throw new IllegalArgumentException(
                    "El estado del proyecto no es válido.");
        }

        String sql = """
            UPDATE proyectos
            SET estado = ?,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id_proyecto = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idProyecto);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al cambiar el estado del proyecto.", e);
        }
    }

    // Convertir una fila de PostgreSQL en un objeto Proyecto.
    private Proyecto mapearProyecto(ResultSet rs)
            throws SQLException {

        Proyecto proyecto = new Proyecto();

        proyecto.setIdProyecto(rs.getInt("id_proyecto"));
        proyecto.setNombre(rs.getString("nombre"));
        proyecto.setDescripcion(rs.getString("descripcion"));
        proyecto.setTecnologia(rs.getString("tecnologia"));
        proyecto.setEstado(rs.getString("estado"));
        proyecto.setIdUsuario(rs.getInt("id_usuario"));

        proyecto.setFechaCreacion(
                convertirFecha(rs.getTimestamp("fecha_creacion")));

        proyecto.setFechaActualizacion(
                convertirFecha(rs.getTimestamp("fecha_actualizacion")));

        return proyecto;
    }

    // Convertir Timestamp a LocalDateTime.
    private java.time.LocalDateTime convertirFecha(Timestamp fecha) {
        return fecha == null ? null : fecha.toLocalDateTime();
    }
}
