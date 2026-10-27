<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String rol = (String) session.getAttribute("rol");

    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (rol == null || !"ADMIN".equalsIgnoreCase(rol)) {
        response.sendRedirect(request.getContextPath() + "/acceso-denegado.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Administración | SoftQuality Solutions</title>

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
        <p class="sidebar-subtitle">Panel administrativo</p>

        <nav class="nav flex-column mt-4">
            <a class="nav-link active"
               href="${pageContext.request.contextPath}/admin/dashboard">
                Inicio
            </a>
            <a class="nav-link"
               href="${pageContext.request.contextPath}/admin/usuarios.jsp">
                Gestión de usuarios
            </a>
            
            <a class="nav-link"
               href="${pageContext.request.contextPath}/proyectos">
                Gestión de proyectos
            </a>
            <a class="nav-link"
               href="${pageContext.request.contextPath}/evaluaciones?vista=admin">
                Evaluaciones y reportes
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
                <h2>Dashboard</h2>
                <p class="text-secondary mb-0">
                    Resumen general del sistema
                </p>
            </div>
            <span class="badge text-bg-primary">ADMINISTRADOR</span>
        </header>

        <section class="welcome-card">
            <h3>¡Bienvenido al panel de administración!</h3>
            <p class="mb-0">
                Desde aquí podrás administrar usuarios y gestionar
                los módulos del sistema SoftQuality Solutions S.A.C.
            </p>
        </section>

        <h5 class="section-heading">Resumen del sistema</h5>

        <section class="row g-3">
            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p>Usuarios registrados</p>
                    <h3>${totalUsuarios != null ? totalUsuarios : '—'}</h3>
                    <small>Número de usuarios registrados</small>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p>Usuarios activos</p>
                    <h3>${usuariosActivos != null ? usuariosActivos : '—'}</h3>
                    <small>Número de usuarios activos</small>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p>Cuentas bloqueadas</p>
                    <h3>${cuentasBloqueadas != null ? cuentasBloqueadas : '—'}</h3>
                    <small>Número de usuarios bloqueados</small>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p>Proyectos registrados</p>
                    <h3>${totalProyectos != null ? totalProyectos : '—'}</h3>
                    <small>Total de proyectos registrados en el sistema</small>
                </div>
            </div>
        </section>

        <h5 class="section-heading">Accesos rápidos</h5>

        <section class="row g-3">
            <div class="col-12 col-md-6">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/admin/usuarios.jsp">
                    <h5>Gestión de usuarios</h5>
                    <p>Administrar cuentas, roles y estados.</p>
                </a>
            </div>

            <div class="col-12 col-md-6">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/proyectos">
                    <h5>Gestión de proyectos</h5>
                    <p>Consultar y administrar proyectos.</p>
                </a>
            </div>

            <div class="col-12 col-md-6">
                <a class="module-card"
                   href="${pageContext.request.contextPath}/evaluaciones?vista=admin">
                    <h5>Evaluaciones y reportes</h5>
                    <p>Acceder a las evaluaciones de calidad.</p>
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