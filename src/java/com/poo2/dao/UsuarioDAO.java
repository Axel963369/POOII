
package com.poo2.dao;

import com.poo2.modelo.Usuario;
import com.poo2.util.Conexion;
import com.poo2.util.HashUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {
    public boolean registrar(Usuario usuario, String password) {

        String verificar = "SELECT id FROM usuarios WHERE email = ?";
        String insertar = "INSERT INTO usuarios(nombre, email, password) VALUES (?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement psVerificar = con.prepareStatement(verificar)) {

            psVerificar.setString(1, usuario.getEmail());

            ResultSet rs = psVerificar.executeQuery();

            if (rs.next()) {
                return false;
            }

            try (PreparedStatement psInsertar = con.prepareStatement(insertar)) {

                psInsertar.setString(1, usuario.getNombre());
                psInsertar.setString(2, usuario.getEmail());
                psInsertar.setString(3, HashUtil.sha256(password));

                psInsertar.executeUpdate();

                return true;
            }

        } catch (Exception e) {
            System.out.println("========== ERROR AL REGISTRAR ==========");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return false;
        }
    }

    public Usuario validar(String email, String password) {

        String sql = """
                SELECT id, nombre, email
                FROM usuarios
                WHERE email = ? AND password = ?
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, HashUtil.sha256(password));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Usuario usuario = new Usuario();

                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));

                return usuario;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
