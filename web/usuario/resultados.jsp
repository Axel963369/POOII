<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.poo2.modelo.Evaluacion"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.time.format.DateTimeFormatter"%>

<%
    String rol = (String) session.getAttribute("rol");

    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (rol == null ||
        (!"USUARIO".equalsIgnoreCase(rol)
        && !"ADMIN".equalsIgnoreCase(rol))) {
        response.sendRedirect(
            request.getContextPath() + "/acceso-denegado.jsp");
        return;
    }

    List<Evaluacion> evaluaciones =
        (List<Evaluacion>) request.getAttribute("evaluaciones");

    if (evaluaciones == null) {
        evaluaciones = new ArrayList<>();
    }

    String errorResultados =
        (String) request.getAttribute("errorResultados");

    DecimalFormat formatoPorcentaje = new DecimalFormat("0.00");
    DateTimeFormatter formatoFecha =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    double sumaPorcentajes = 0;
    int totalEvaluaciones = evaluaciones.size();
    int excelentes = 0;
    int bajas = 0;

    for (Evaluacion e : evaluaciones) {
        sumaPorcentajes += e.getPorcentajeCumplimiento();

        if ("EXCELENTE".equalsIgnoreCase(e.getNivelCalidad())) {
            excelentes++;
        }

        if ("BAJA".equalsIgnoreCase(e.getNivelCalidad())) {
            bajas++;
        }
    }

    double promedio = totalEvaluaciones > 0
        ? sumaPorcentajes / totalEvaluaciones : 0;
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Resultados y reportes | SoftQuality Solutions</title>

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

    .dashboard-sidebar a:hover,
    .dashboard-sidebar a.active {
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

    .stat-card,
    .content-card {
        background: white;
        border: 1px solid #e2e8f0;
        border-radius: 14px;
        padding: 22px;
        box-sizing: border-box;
        box-shadow: 0 4px 14px rgba(23, 37, 84, 0.04);
        height: 100%;
    }

    .stat-label {
        color: #64748b;
        font-size: 0.92rem;
        margin-bottom: 10px;
    }

    .stat-number {
        font-size: 1.9rem;
        font-weight: 700;
        color: #172554;
        margin-bottom: 5px;
    }

    .stat-description {
        color: #64748b;
        font-size: 0.85rem;
    }

    .dashboard-main h1,
    .dashboard-main h2,
    .dashboard-main h3,
    .dashboard-main h4 {
        color: #172554;
    }

    .progress {
        height: 9px;
        background: #e2e8f0;
        border-radius: 20px;
    }

    .progress-bar {
        border-radius: 20px;
    }

    .table thead th {
        color: #475569;
        background: #f8fafc;
        white-space: nowrap;
        font-size: 0.88rem;
    }

    .table td {
        vertical-align: middle;
    }

    .empty-state {
        text-align: center;
        padding: 35px 15px;
        color: #64748b;
    }

    .sidebar-footer {
        margin-top: 35px;
    }

    .dashboard-footer {
        margin-top: 35px;
        padding-top: 20px;
        border-top: 1px solid #e2e8f0;
        color: #64748b;
        font-size: 0.88rem;
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
            <a class="nav-link"
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

            <a class="nav-link active"
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
                <h2>Resultados y reportes</h2>
                <p class="text-secondary mb-0">
                    Consulta y analiza la calidad arquitectónica de tus proyectos.
                </p>
            </div>

            <span class="badge text-bg-primary">ANÁLISIS MVC</span>
        </header>

        <% if (errorResultados != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= errorResultados %>
            </div>
        <% } %>

        <section class="row g-3 mb-4">

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <div class="stat-label">Evaluaciones registradas</div>
                    <div class="stat-number"><%= totalEvaluaciones %></div>
                    <div class="stat-description">
                        Evaluaciones guardadas en el historial
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <div class="stat-label">Promedio de cumplimiento</div>
                    <div class="stat-number">
                        <%= formatoPorcentaje.format(promedio) %>%
                    </div>
                    <div class="stat-description">
                        Promedio de todas tus evaluaciones
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <div class="stat-label">Calidad excelente</div>
                    <div class="stat-number text-success"><%= excelentes %></div>
                    <div class="stat-description">
                        Evaluaciones con resultado excelente
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <div class="stat-label">Calidad baja</div>
                    <div class="stat-number text-danger"><%= bajas %></div>
                    <div class="stat-description">
                        Evaluaciones que requieren atención
                    </div>
                </div>
            </div>

        </section>

        <section class="content-card mb-4">
            <h4 class="mb-2">Historial de evaluaciones</h4>
            <p class="text-secondary">
                Las evaluaciones más recientes aparecen primero.
                Registrar una nueva evaluación no elimina las anteriores.
            </p>

            <% if (evaluaciones.isEmpty()) { %>

                <div class="empty-state">
                    <h5>No hay evaluaciones registradas</h5>
                    <p>
                        Registra una evaluación MVC para consultar aquí
                        los porcentajes y niveles de calidad.
                    </p>
                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/evaluaciones">
                        Registrar evaluación
                    </a>
                </div>

            <% } else { %>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Proyecto</th>
                                <th>Fecha</th>
                                <th>Cumplimiento</th>
                                <th>Nivel de calidad</th>
                                <th>Estado</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% for (Evaluacion e : evaluaciones) {
                                double porcentaje =
                                    e.getPorcentajeCumplimiento();

                                String colorBarra;

                                if (porcentaje >= 90) {
                                    colorBarra = "bg-success";
                                } else if (porcentaje >= 75) {
                                    colorBarra = "bg-primary";
                                } else if (porcentaje >= 60) {
                                    colorBarra = "bg-warning";
                                } else {
                                    colorBarra = "bg-danger";
                                }

                                String nivel = e.getNivelCalidad() == null
                                    ? "PENDIENTE" : e.getNivelCalidad();

                                String estado = e.getEstado() == null
                                    ? "SIN ESTADO" : e.getEstado();

                                String colorNivel;

                                switch (nivel.toUpperCase()) {
                                    case "EXCELENTE":
                                        colorNivel = "text-bg-success";
                                        break;
                                    case "BUENA":
                                        colorNivel = "text-bg-primary";
                                        break;
                                    case "ACEPTABLE":
                                        colorNivel = "text-bg-warning";
                                        break;
                                    case "BAJA":
                                        colorNivel = "text-bg-danger";
                                        break;
                                    default:
                                        colorNivel = "text-bg-secondary";
                                }
                            %>

                            <tr>
                                <td>
                                    <strong>
                                        <%= e.getNombreProyecto() == null
                                            ? "Proyecto #" + e.getIdProyecto()
                                            : e.getNombreProyecto() %>
                                    </strong>
                                    <div class="text-secondary small">
                                        Evaluación #<%= e.getIdEvaluacion() %>
                                    </div>
                                </td>

                                <td>
                                    <% if (e.getFechaEvaluacion() != null) { %>
                                        <%= e.getFechaEvaluacion()
                                            .format(formatoFecha) %>
                                    <% } else { %>
                                        Sin fecha
                                    <% } %>
                                </td>

                                <td style="min-width: 150px;">
                                    <div class="d-flex justify-content-between mb-1">
                                        <strong>
                                            <%= formatoPorcentaje.format(porcentaje) %>%
                                        </strong>
                                    </div>
                                    <div class="progress">
                                        <div class="progress-bar <%= colorBarra %>"
                                             role="progressbar"
                                             style="width: <%= Math.max(0, Math.min(100, porcentaje)) %>%"
                                             aria-valuenow="<%= porcentaje %>"
                                             aria-valuemin="0"
                                             aria-valuemax="100">
                                        </div>
                                    </div>
                                </td>

                                <td>
                                    <span class="badge <%= colorNivel %>">
                                        <%= nivel %>
                                    </span>
                                </td>

                                <td>
                                    <span class="badge text-bg-light border">
                                        <%= estado %>
                                    </span>
                                </td>
                            </tr>

                            <% } %>
                        </tbody>
                    </table>
                </div>

            <% } %>
        </section>

        <% if (evaluaciones.size() >= 2) { %>
            <section class="content-card">
                <h4 class="mb-2">Comparación de evaluaciones</h4>
                <p class="text-secondary">
                    Compara la evaluación más reciente con la anterior
                    de cada proyecto para observar su evolución.
                </p>

                <%
                    java.util.Map<Integer, Evaluacion> ultimaPorProyecto =
                        new java.util.LinkedHashMap<>();

                    java.util.Map<Integer, Evaluacion> anteriorPorProyecto =
                        new java.util.LinkedHashMap<>();

                    for (Evaluacion e : evaluaciones) {
                        int idProyecto = e.getIdProyecto();

                        if (!ultimaPorProyecto.containsKey(idProyecto)) {
                            ultimaPorProyecto.put(idProyecto, e);
                        } else if (!anteriorPorProyecto.containsKey(idProyecto)) {
                            anteriorPorProyecto.put(idProyecto, e);
                        }
                    }

                    boolean hayComparacion = false;

                    for (Integer idProyecto : ultimaPorProyecto.keySet()) {
                        Evaluacion ultima = ultimaPorProyecto.get(idProyecto);
                        Evaluacion anterior = anteriorPorProyecto.get(idProyecto);

                        if (anterior != null) {
                            hayComparacion = true;

                            double diferencia =
                                ultima.getPorcentajeCumplimiento()
                                - anterior.getPorcentajeCumplimiento();

                            String textoDiferencia =
                                diferencia > 0 ? "+" : "";
                %>

                    <div class="border rounded-3 p-3 mb-3">
                        <div class="d-flex justify-content-between flex-wrap gap-2">
                            <strong>
                                <%= ultima.getNombreProyecto() == null
                                    ? "Proyecto #" + idProyecto
                                    : ultima.getNombreProyecto() %>
                            </strong>

                            <span class="<%= diferencia > 0
                                ? "text-success"
                                : diferencia < 0
                                    ? "text-danger"
                                    : "text-secondary" %> fw-bold">
                                <%= textoDiferencia
                                    + formatoPorcentaje.format(diferencia) %>
                                puntos porcentuales
                            </span>
                        </div>

                        <div class="text-secondary small mt-2">
                            Anterior:
                            <%= formatoPorcentaje.format(
                                anterior.getPorcentajeCumplimiento()) %>%
                            &nbsp; → &nbsp;
                            Más reciente:
                            <%= formatoPorcentaje.format(
                                ultima.getPorcentajeCumplimiento()) %>%
                        </div>
                    </div>

                <%
                        }
                    }

                    if (!hayComparacion) {
                %>
                    <div class="alert alert-info mb-0">
                        Todavía no hay dos evaluaciones del mismo proyecto
                        para comparar. Cuando vuelvas a evaluar uno de tus
                        proyectos, aquí podrás ver si mejoró o retrocedió.
                    </div>
                <%
                    }
                %>
            </section>
        <% } %>

        <footer class="dashboard-footer">
            SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
        </footer>

    </main>
</div>

<script src="${pageContext.request.contextPath}/bootstrap/bootstrap.bundle.min.js"></script>

</body>
</html>