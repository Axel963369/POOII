<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html> <head> <meta charset="UTF-8"> <title>Crear cuenta · Tech Login</title>

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
                ◎
            </div>

            <h1>Crear cuenta</h1>

            <p>
                Registra un nuevo usuario en el sistema
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

        <form action="<%= request.getContextPath() %>/registro"
              method="POST">

            <div class="form-group">

                <label for="nombre">
                    Nombre completo
                </label>

                <input type="text"
                       id="nombre"
                       name="nombre"
                       placeholder="Ingresa tu nombre"
                       required>

            </div>

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
                       placeholder="Ingresa una contraseña"
                       required>

            </div>

            <button type="submit"
                    class="btn btn-primary form-submit">

                Crear cuenta

            </button>

        </form>

        <div class="form-footer">

            ¿Ya tienes una cuenta?

            <a href="<%= request.getContextPath() %>/login.jsp">
                Iniciar sesión
            </a>

            <br><br>

            <a href="<%= request.getContextPath() %>/index.jsp">
                ← Volver al inicio
            </a>

        </div>

    </div>

</main>

</body> </html>