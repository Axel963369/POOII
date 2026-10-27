<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String rol = (String) session.getAttribute("rol");

    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (rol == null ||
        (!"USUARIO".equalsIgnoreCase(rol)
        && !"ADMIN".equalsIgnoreCase(rol))) {
        response.sendRedirect(request.getContextPath() + "/acceso-denegado.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Proyectos y evaluaciones | SoftQuality Solutions</title>

    <link rel="stylesheet"
      href="${pageContext.request.contextPath}/bootstrap/bootstrap.min.css">
    
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-body">
<style>
    body.dashboard-body {
        margin: 0;
        background: #f4f7fc;
        color: #172554;
        font-family: Arial, sans-serif;
    }

    .dashboard-layout {
        display: grid !important;
        grid-template-columns: 250px minmax(0, 1fr) !important;
        min-height: 100vh;
    }

    .dashboard-sidebar {
        background: #172554 !important;
        color: white !important;
        min-height: 100vh;
        padding: 25px 15px;
        box-sizing: border-box;
    }

    .dashboard-sidebar a {
        display: block;
        color: #dbeafe;
        text-decoration: none;
        padding: 12px 14px;
        margin: 5px 0;
        border-radius: 8px;
        transition: background 0.2s;
    }

    .dashboard-sidebar a:hover {
        background: #263b70;
        color: white;
    }

    .dashboard-main {
        min-width: 0;
        padding: 30px;
        background: #f4f7fc;
        box-sizing: border-box;
    }

    .dashboard-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 15px;
        margin-bottom: 30px;
        flex-wrap: wrap;
    }

    .welcome-card {
        max-width: none !important;
        width: 100%;
        box-sizing: border-box;
        background: white;
        padding: 28px;
        border-radius: 16px;
        margin: 0 0 25px;
        box-shadow: 0 4px 18px rgba(23, 37, 84, 0.06);
    }

    .stat-card,
    .module-card {
        background: white;
        border: 1px solid #e2e8f0;
        border-radius: 14px;
        padding: 22px;
        box-sizing: border-box;
        box-shadow: 0 4px 14px rgba(23, 37, 84, 0.04);
    }

    a.module-card {
        display: block;
        color: #172554;
        text-decoration: none;
        transition: transform 0.2s, box-shadow 0.2s;
    }

    a.module-card:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 22px rgba(23, 37, 84, 0.10);
    }

    .dashboard-main h1,
    .dashboard-main h2,
    .dashboard-main h3 {
        color: #172554;
    }

    @media (max-width: 768px) {
        .dashboard-layout {
            grid-template-columns: 1fr !important;
        }

        .dashboard-sidebar {
            min-height: auto;
        }

        .dashboard-main {
            padding: 18px;
        }

        .dashboard-header {
            align-items: flex-start;
        }
    }
</style>
<div class="dashboard-layout">
    <aside class="dashboard-sidebar">
        <h4 class="brand-title">SoftQuality</h4>
        <p class="sidebar-subtitle">Área de trabajo</p>

        <nav class="nav flex-column mt-4">
            <a class="nav-link active"
               href="${pageContext.request.contextPath}/usuario/dashboard.jsp">
                Inicio
            </a>
            <a class="nav-link"
               href="${pageContext.request.contextPath}/proyectos">
                Mis proyectos
            </a>
            <a class="nav-link"
               href="${pageContext.request.contextPath}/evaluaciones">
                Evaluaciones MVC
            </a>
            <a class="nav-link"
               href="${pageContext.request.contextPath}/historial-evaluaciones">
                Resultados y reportes
            </a>
        </nav>

        <div class="sidebar-footer">
            <a class="btn btn-outline-light w-100"
               href="${pageContext.request.contextPath}/logout">
                Cerrar sesión
            </a>
        </div>
    </aside>

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div>
                <h2>Mi espacio de trabajo</h2>
                <p class="text-secondary mb-0">
                    Proyectos y evaluación de calidad del software
                </p>
            </div>
            <span class="badge text-bg-success">USUARIO</span>
        </header>

        <section class="welcome-card">
            <h3>¡Bienvenido!</h3>
            <p class="mb-0">
                Desde aquí podrás registrar tus proyectos, ejecutar
                evaluaciones y consultar sus resultados.
            </p>
        </section>

        <h5 class="section-heading">Mis actividades</h5>

        <section class="row g-3">
            <div class="col-12 col-md-4">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/proyectos">
                    <h5>Mis proyectos</h5>
                    <p>Registra y consulta proyectos de software.</p>
                </a>
            </div>

            <div class="col-12 col-md-4">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/evaluaciones">
                    <h5>Evaluaciones MVC</h5>
                    <p>Evalúa la calidad arquitectónica de tus proyectos.</p>
                </a>
            </div>

            <div class="col-12 col-md-4">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/historial-evaluaciones">
                    <h5>Resultados</h5>
                    <p>Consulta los resultados y reportes disponibles.</p>
                </a>
            </div>
        </section>

        <footer class="dashboard-footer">
            SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
        </footer>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>