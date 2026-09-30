<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html> <head> <meta charset="UTF-8"> <title>Tech Login System</title> <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css"> </head>

<body>

<nav class="navbar">
    <div class="logo">
        ◈ <span>TECH</span> LOGIN SYSTEM
    </div>
</nav>

<main class="page-container">

    <div class="content hero">

        <div class="tech-icon">
            ◈
        </div>

        <h1>
            Sistema de Gestión
            <span class="highlight">Tecnológica</span>
        </h1>

        <p>
            Plataforma web desarrollada con Java, JSP y
            PostgreSQL(Neon) para la gestión
            segura de usuarios.
        </p>

        <div class="buttons">

            <a class="btn btn-primary"
               href="<%= request.getContextPath() %>/login.jsp">
                Iniciar sesión
            </a>

            <a class="btn btn-secondary"
               href="<%= request.getContextPath() %>/registro.jsp">
                Crear cuenta
            </a>

        </div>

    </div>

</main>

<footer class="footer">
    Proyecto POO II · Java Web Application
</footer>

</body> </html>
