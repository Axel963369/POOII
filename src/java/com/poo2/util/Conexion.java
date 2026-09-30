
package com.poo2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
     public static Connection getConexion() throws SQLException {

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        System.out.println("========== DATOS DE CONEXION ==========");
        System.out.println("DB_URL: " + url);
        System.out.println("DB_USER: " + usuario);
        System.out.println("DB_PASSWORD configurada: "
                + (password != null && !password.isEmpty()));
        System.out.println("=======================================");

        if (url == null || usuario == null || password == null) {
            throw new SQLException(
                    "No se encontraron las variables de entorno."
            );
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "No se encontró el driver de PostgreSQL.",
                    e
            );
        }

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }
}
