<%@ page import="com.poo2.modelo.Usuario" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html> <head> <meta charset="UTF-8"> <title>Bienvenido · Tech Login</title>

<link rel="stylesheet"
      href="<%= request.getContextPath() %>/css/style.css">

</head>

<body>

<nav class="navbar">

    <div class="logo">
        ◈ <span>TECH</span> LOGIN SYSTEM
    </div>

</nav>

<main class="page-container">

    <div class="card welcome-card">

    <%
        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        if (usuario == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp"
            );

            return;
        }
    %>

        <div class="welcome-icon">
            ✓
        </div>

        <h1>
            Bienvenido,
            <span><%= usuario.getNombre() %></span>
        </h1>

        <p>
            Has iniciado sesión correctamente
            en el sistema.
        </p>

        <div class="user-info">

            <p>
                <strong>Usuario:</strong>
                <span><%= usuario.getNombre() %></span>
            </p>

            <br>

            <p>
                <strong>Correo:</strong>
                <span><%= usuario.getEmail() %></span>
            </p>

        </div>

        <a href="<%= request.getContextPath() %>/logout"
           class="btn btn-secondary">

            Cerrar sesión

        </a>

    </div>

</main>

<footer class="footer">
    Sesión activa · Sistema de Usuarios
</footer>

</body> </html>