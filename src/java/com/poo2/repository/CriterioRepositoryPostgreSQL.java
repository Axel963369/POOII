
package com.poo2.repository;

import com.poo2.modelo.Criterio;
import com.poo2.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CriterioRepositoryPostgreSQL implements CriterioRepository{
    @Override
    public List<Criterio> listarActivos() {
        List<Criterio> criterios = new ArrayList<>();

        String sql = """
            SELECT id_criterio, codigo, nombre, descripcion,
                   categoria, peso, activo
            FROM criterios
            WHERE activo = TRUE
            ORDER BY categoria, codigo
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Criterio criterio = new Criterio();

                criterio.setIdCriterio(rs.getInt("id_criterio"));
                criterio.setCodigo(rs.getString("codigo"));
                criterio.setNombre(rs.getString("nombre"));
                criterio.setDescripcion(rs.getString("descripcion"));
                criterio.setCategoria(rs.getString("categoria"));
                criterio.setPeso(rs.getInt("peso"));
                criterio.setActivo(rs.getBoolean("activo"));

                criterios.add(criterio);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al listar los criterios de evaluación.", e
            );
        }

        return criterios;
    }

    @Override
    public Criterio buscarPorId(int idCriterio) {
        String sql = """
            SELECT id_criterio, codigo, nombre, descripcion,
                   categoria, peso, activo
            FROM criterios
            WHERE id_criterio = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCriterio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Criterio criterio = new Criterio();

                    criterio.setIdCriterio(rs.getInt("id_criterio"));
                    criterio.setCodigo(rs.getString("codigo"));
                    criterio.setNombre(rs.getString("nombre"));
                    criterio.setDescripcion(rs.getString("descripcion"));
                    criterio.setCategoria(rs.getString("categoria"));
                    criterio.setPeso(rs.getInt("peso"));
                    criterio.setActivo(rs.getBoolean("activo"));

                    return criterio;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al buscar el criterio.", e
            );
        }

        return null;
    }

    @Override
    public boolean registrar(Criterio criterio) {
        String sql = """
            INSERT INTO criterios
                (codigo, nombre, descripcion, categoria, peso, activo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, criterio.getCodigo());
            ps.setString(2, criterio.getNombre());
            ps.setString(3, criterio.getDescripcion());
            ps.setString(4, criterio.getCategoria());
            ps.setInt(5, criterio.getPeso());
            ps.setBoolean(6, criterio.isActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al registrar el criterio.", e
            );
        }
    }

    @Override
    public boolean actualizar(Criterio criterio) {
        String sql = """
            UPDATE criterios
            SET codigo = ?,
                nombre = ?,
                descripcion = ?,
                categoria = ?,
                peso = ?,
                activo = ?
            WHERE id_criterio = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, criterio.getCodigo());
            ps.setString(2, criterio.getNombre());
            ps.setString(3, criterio.getDescripcion());
            ps.setString(4, criterio.getCategoria());
            ps.setInt(5, criterio.getPeso());
            ps.setBoolean(6, criterio.isActivo());
            ps.setInt(7, criterio.getIdCriterio());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al actualizar el criterio.", e
            );
        }
    }

    @Override
    public boolean cambiarEstado(int idCriterio, boolean activo) {
        String sql = """
            UPDATE criterios
            SET activo = ?
            WHERE id_criterio = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idCriterio);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al cambiar el estado del criterio.", e
            );
        }
    }
}
