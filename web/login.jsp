<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html> <head> <meta charset="UTF-8"> <title>Iniciar sesión · Tech Login</title>

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

    <div class="card">

        <div class="card-header">

            <div class="small-icon">
                ◉
            </div>

            <h1>Iniciar sesión</h1>

            <p>
                Accede a tu cuenta para continuar
            </p>

        </div>

        <%
            String error = (String) request.getAttribute("error");

            if (error != null) {
        %>

            <div class="error-message">
                <%= error %>
            </div>

        <%
            }
        %>

        <form action="<%= request.getContextPath() %>/login"
              method="POST">

            <div class="form-group">

                <label for="email">
                    Correo electrónico
                </label>

                <input type="email"
                       id="email"
                       name="email"
                       placeholder="ejemplo@correo.com"
                       required>

            </div>

            <div class="form-group">

                <label for="password">
                    Contraseña
                </label>

                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Ingresa tu contraseña"
                       required>

            </div>

            <button type="submit"
                    class="btn btn-primary form-submit">

                Iniciar sesión

            </button>

        </form>

        <div class="form-footer">

            ¿No tienes una cuenta?

            <a href="<%= request.getContextPath() %>/registro.jsp">
                Crear cuenta
            </a>

            <br><br>

            <a href="<%= request.getContextPath() %>/index.jsp">
                ← Volver al inicio
            </a>

        </div>

    </div>

</main>

</body> </html>