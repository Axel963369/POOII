<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acceso denegado</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <main>
        <h1>Acceso denegado</h1>
        <p>No tienes permisos para acceder a esta sección.</p>
        <a href="${pageContext.request.contextPath}/bienvenido.jsp">
            Volver al inicio
        </a>
    </main>
</body>
</html>