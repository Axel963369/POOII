
package com.poo2.repository;

import com.poo2.modelo.CuentaUsuario;
import com.poo2.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsuarioRepositoryPostgreSQL implements UsuarioRepository{
    @Override
    public CuentaUsuario buscarPorEmail(String email) {
        String sql = """
            SELECT u.id, u.nombre, u.email, u.password,
                   u.rol_id, r.nombre AS rol_nombre,
                   u.permiso_id, u.ultimo_acceso,
                   u.intentos_fallidos, u.bloqueado,
                   u.token_recuperacion, u.fecha_creacion,
                   u.fecha_actualizacion, u.usuario_registro,
                   u.estado
            FROM usuarios u
            JOIN roles r ON r.rol_id = u.rol_id
            WHERE LOWER(u.email) = LOWER(?)
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCuenta(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el usuario.", e);
        }

        return null;
    }

    @Override
    public boolean registrar(CuentaUsuario cuenta,
                             String passwordHash) {
        String sql = """
            INSERT INTO usuarios
                (nombre, email, password, rol_id, permiso_id,
                 intentos_fallidos, bloqueado, fecha_creacion,
                 fecha_actualizacion, usuario_registro, estado)
            VALUES (?, ?, ?, ?, ?, 0, FALSE,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, 'ACTIVO')
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cuenta.getNombreUsuario());
            ps.setString(2, cuenta.getEmail());
            ps.setString(3, passwordHash);

            int rolId = obtenerRolUsuario(con);
            ps.setInt(4, rolId);

            if (cuenta.getPermisoId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, cuenta.getPermisoId());
            }

            ps.setString(6, cuenta.getUsuarioRegistro());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return false; // Correo duplicado.
            }
            throw new IllegalStateException(
                    "Error al registrar el usuario.", e);
        }
    }

    private int obtenerRolUsuario(Connection con)
            throws SQLException {
        String sql =
                "SELECT rol_id FROM roles WHERE nombre = 'USUARIO'";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("rol_id");
            }
        }

        throw new SQLException(
                "No existe el rol USUARIO en la tabla roles.");
    }

    @Override
    public boolean registrarIntentoFallido(String email) {
        String sql = """
            UPDATE usuarios
            SET intentos_fallidos = intentos_fallidos + 1,
                bloqueado = CASE
                    WHEN intentos_fallidos + 1 >= 3 THEN TRUE
                    ELSE bloqueado
                END,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE LOWER(email) = LOWER(?)
              AND bloqueado = FALSE
              AND estado = 'ACTIVO'
            RETURNING bloqueado
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("bloqueado");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al registrar el intento fallido.", e);
        }
    }

    @Override
    public void registrarAccesoExitoso(int idUsuario) {
        String sql = """
            UPDATE usuarios
            SET intentos_fallidos = 0,
                ultimo_acceso = CURRENT_TIMESTAMP,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id = ?
              AND bloqueado = FALSE
              AND estado = 'ACTIVO'
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "La cuenta está bloqueada o inactiva.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo registrar el acceso.", e);
        }
    }

    @Override
    public List<CuentaUsuario> listar() {
        String sql = """
            SELECT u.id, u.nombre, u.email, u.password,
                   u.rol_id, r.nombre AS rol_nombre,
                   u.permiso_id, u.ultimo_acceso,
                   u.intentos_fallidos, u.bloqueado,
                   u.token_recuperacion, u.fecha_creacion,
                   u.fecha_actualizacion, u.usuario_registro,
                   u.estado
            FROM usuarios u
            JOIN roles r ON r.rol_id = u.rol_id
            ORDER BY u.id
            """;

        List<CuentaUsuario> cuentas = new ArrayList<>();

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cuentas.add(mapearCuenta(rs));
            }
            return cuentas;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar los usuarios.", e);
        }
    }

    @Override
    public CuentaUsuario buscarPorId(int idUsuario) {
        String sql = """
            SELECT u.id, u.nombre, u.email, u.password,
                   u.rol_id, r.nombre AS rol_nombre,
                   u.permiso_id, u.ultimo_acceso,
                   u.intentos_fallidos, u.bloqueado,
                   u.token_recuperacion, u.fecha_creacion,
                   u.fecha_actualizacion, u.usuario_registro,
                   u.estado
            FROM usuarios u
            JOIN roles r ON r.rol_id = u.rol_id
            WHERE u.id = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCuenta(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el usuario por ID.", e);
        }

        return null;
    }

    @Override
    public boolean actualizar(CuentaUsuario cuenta) {
        String sql = """
            UPDATE usuarios
            SET nombre = ?, email = ?, rol_id = ?,
                permiso_id = ?, estado = ?,
                fecha_actualizacion = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cuenta.getNombreUsuario());
            ps.setString(2, cuenta.getEmail());
            ps.setInt(3, cuenta.getRolId());

            if (cuenta.getPermisoId() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, cuenta.getPermisoId());
            }

            ps.setString(5, cuenta.getEstado());
            ps.setInt(6, cuenta.getIdUsuario());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el usuario.", e);
        }
    }

    @Override
    public boolean eliminar(int idUsuario) {
        String sql = """
        UPDATE usuarios
        SET estado = 'INACTIVO',
            fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id = ?
          AND estado <> 'INACTIVO'
        """;
        try (Connection con = Conexion.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al desactivar el usuario.", e);
        }
    }
    
    @Override
    public boolean desbloquear(int idUsuario) {
    String sql = """
        UPDATE usuarios
        SET bloqueado = FALSE,
            intentos_fallidos = 0,
            fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id = ?
          AND bloqueado = TRUE
        """;
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idUsuario);
        return ps.executeUpdate() == 1;
    } catch (SQLException e) {
        throw new IllegalStateException(
                "Error al desbloquear el usuario.", e);
    }
    }
    
    @Override
    public Map<String, Integer> obtenerEstadisticasUsuarios() {
        String sql = """
        SELECT
            COUNT(*) AS total,
            COUNT(*) FILTER (
                WHERE estado = 'ACTIVO'
            ) AS activos,
            COUNT(*) FILTER (
                WHERE bloqueado = TRUE
            ) AS bloqueados
        FROM usuarios
        """;
        Map<String, Integer> estadisticas = new java.util.HashMap<>();
        try (Connection con = Conexion.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                estadisticas.put("total", rs.getInt("total"));
                estadisticas.put("activos", rs.getInt("activos"));
                estadisticas.put("bloqueados", rs.getInt("bloqueados"));
            }
            return estadisticas;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar las estadísticas de usuarios.", e);
        }
    }
    
    @Override
    public Map<Integer, String> listarRoles() {
    String sql = """
        SELECT rol_id, nombre
        FROM roles
        ORDER BY nombre
        """;

    Map<Integer, String> roles = new LinkedHashMap<>();

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            roles.put(
                rs.getInt("rol_id"),
                rs.getString("nombre")
            );
        }

        return roles;
    } catch (SQLException e) {
        throw new IllegalStateException(
            "Error al listar los roles.", e
        );
    }
    }

    private CuentaUsuario mapearCuenta(ResultSet rs)
            throws SQLException {
        CuentaUsuario cuenta = new CuentaUsuario();

        cuenta.setIdUsuario(rs.getInt("id"));
        cuenta.setNombreUsuario(rs.getString("nombre"));
        cuenta.setEmail(rs.getString("email"));
        cuenta.setPasswordHash(rs.getString("password"));
        cuenta.setRolId(rs.getInt("rol_id"));
        cuenta.setRolNombre(rs.getString("rol_nombre"));

        int permisoId = rs.getInt("permiso_id");
        if (!rs.wasNull()) {
            cuenta.setPermisoId(permisoId);
        }

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            cuenta.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        cuenta.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        cuenta.setBloqueado(rs.getBoolean("bloqueado"));
        cuenta.setTokenRecuperacion(
                rs.getString("token_recuperacion"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            cuenta.setFechaCreacion(
                    fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion =
                rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            cuenta.setFechaActualizacion(
                    fechaActualizacion.toLocalDateTime());
        }

        cuenta.setUsuarioRegistro(rs.getString("usuario_registro"));
        cuenta.setEstado(rs.getString("estado"));

        return cuenta;
    }
}
