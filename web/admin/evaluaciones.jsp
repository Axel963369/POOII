<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.poo2.modelo.Evaluacion"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%
    String rol = (String) session.getAttribute("rol");

    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (rol == null || !"ADMIN".equalsIgnoreCase(rol)) {
        response.sendRedirect(
            request.getContextPath() + "/acceso-denegado.jsp");
        return;
    }

    List<Evaluacion> evaluaciones =
        (List<Evaluacion>) request.getAttribute("evaluaciones");

    Map<String, Object> estadisticas =
        (Map<String, Object>) request.getAttribute("estadisticas");

    Evaluacion detalle =
        (Evaluacion) request.getAttribute("detalleEvaluacion");

    List<Map<String, Object>> respuestasDetalle =
        (List<Map<String, Object>>) request.getAttribute("respuestasDetalle");

    String error = (String) request.getAttribute("errorEvaluaciones");

    DateTimeFormatter formatoFecha =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    if (evaluaciones == null) {
        evaluaciones = java.util.Collections.emptyList();
    }

    if (estadisticas == null) {
        estadisticas = java.util.Collections.emptyMap();
    }

    if (respuestasDetalle == null) {
        respuestasDetalle = java.util.Collections.emptyList();
    }

    Object totalObj = estadisticas.get("totalEvaluaciones");
    Object proyectosObj = estadisticas.get("proyectosEvaluados");
    Object promedioObj = estadisticas.get("promedioCumplimiento");
    Object deficienciasObj = estadisticas.get("totalDeficiencias");

    int totalEvaluaciones = totalObj instanceof Number
        ? ((Number) totalObj).intValue() : 0;

    int proyectosEvaluados = proyectosObj instanceof Number
        ? ((Number) proyectosObj).intValue() : 0;

    double promedioCumplimiento = promedioObj instanceof Number
        ? ((Number) promedioObj).doubleValue() : 0.0;

    int totalDeficiencias = deficienciasObj instanceof Number
        ? ((Number) deficienciasObj).intValue() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Evaluaciones y reportes | SoftQuality Solutions</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/bootstrap/bootstrap.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

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
            margin-bottom: 25px;
            flex-wrap: wrap;
        }

        .dashboard-main h1,
        .dashboard-main h2,
        .dashboard-main h3,
        .dashboard-main h4 {
            color: #172554;
        }

        .welcome-card,
        .stat-card,
        .content-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            padding: 22px;
            box-sizing: border-box;
            box-shadow: 0 4px 14px rgba(23, 37, 84, 0.05);
        }

        .welcome-card {
            margin-bottom: 24px;
            border: none;
            padding: 27px;
        }

        .stat-card {
            height: 100%;
        }

        .stat-label {
            color: #64748b;
            font-size: 0.92rem;
            margin-bottom: 12px;
        }

        .stat-value {
            font-size: 1.9rem;
            font-weight: 700;
            margin: 0;
        }

        .stat-note {
            color: #64748b;
            font-size: 0.8rem;
            margin-top: 8px;
            margin-bottom: 0;
        }

        .section-heading {
            font-weight: 700;
            margin: 28px 0 15px;
        }

        .table-responsive {
            border-radius: 10px;
        }

        .table {
            vertical-align: middle;
            margin-bottom: 0;
        }

        .table thead th {
            background: #f8fafc;
            color: #475569;
            font-size: 0.82rem;
            white-space: nowrap;
        }

        .table td {
            font-size: 0.9rem;
        }

        .quality-badge {
            display: inline-block;
            padding: 6px 10px;
            border-radius: 20px;
            font-size: 0.77rem;
            font-weight: 700;
            white-space: nowrap;
        }

        .quality-excelente {
            color: #166534;
            background: #dcfce7;
        }

        .quality-buena {
            color: #1d4ed8;
            background: #dbeafe;
        }

        .quality-aceptable {
            color: #92400e;
            background: #fef3c7;
        }

        .quality-baja {
            color: #991b1b;
            background: #fee2e2;
        }

        .quality-default {
            color: #475569;
            background: #e2e8f0;
        }

        .btn-details {
            color: #1d4ed8;
            text-decoration: none;
            font-weight: 600;
            white-space: nowrap;
        }

        .btn-details:hover {
            text-decoration: underline;
        }

        .detail-row {
            background: #f8fafc;
        }

        .detail-label {
            color: #64748b;
            font-size: 0.85rem;
            margin-bottom: 4px;
        }

        .detail-value {
            font-weight: 600;
            overflow-wrap: anywhere;
        }

        .progress {
            height: 9px;
            background: #e2e8f0;
        }

        .progress-bar {
            background: #2563eb;
        }

        .empty-state {
            text-align: center;
            padding: 35px 15px;
            color: #64748b;
        }

        .dashboard-footer {
            margin-top: 30px;
            padding-top: 18px;
            border-top: 1px solid #e2e8f0;
            color: #64748b;
            font-size: 0.85rem;
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

            .stat-value {
                font-size: 1.6rem;
            }
        }
    </style>
</head>

<body class="dashboard-body">
<div class="dashboard-layout">

    <aside class="dashboard-sidebar">
        <h4 class="brand-title">SoftQuality</h4>
        <p class="sidebar-subtitle">Panel administrativo</p>

        <nav class="nav flex-column mt-4">
            <a class="nav-link"
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

            <a class="nav-link active"
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
                <h2>Evaluaciones y reportes</h2>
                <p class="text-secondary mb-0">
                    Historial y calidad arquitectónica de los proyectos MVC
                </p>
            </div>

            <span class="badge text-bg-primary">ADMINISTRADOR</span>
        </header>

        <section class="welcome-card">
            <h4>Panel de evaluación arquitectónica</h4>
            <p class="text-secondary mb-0">
                Consulta los resultados registrados, identifica
                deficiencias y revisa el cumplimiento de las buenas
                prácticas de arquitectura MVC.
            </p>
        </section>

        <% if (error != null) { %>
            <div class="alert alert-danger" role="alert">
                <strong>No se pudo cargar el reporte.</strong>
                <%= error %>
            </div>
        <% } %>

        <h5 class="section-heading">Resumen estadístico</h5>

        <section class="row g-3">
            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p class="stat-label">Evaluaciones completadas</p>
                    <h3 class="stat-value"><%= totalEvaluaciones %></h3>
                    <p class="stat-note">Evaluaciones registradas</p>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p class="stat-label">Proyectos evaluados</p>
                    <h3 class="stat-value"><%= proyectosEvaluados %></h3>
                    <p class="stat-note">Proyectos con evaluación completada</p>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p class="stat-label">Cumplimiento promedio</p>
                    <h3 class="stat-value"><%= String.format(java.util.Locale.US, "%.2f", promedioCumplimiento) %>%</h3>
                    <div class="progress mt-3">
                        <div class="progress-bar"
                             role="progressbar"
                             style="width: <%= Math.max(0, Math.min(100, promedioCumplimiento)) %>%"
                             aria-valuenow="<%= promedioCumplimiento %>"
                             aria-valuemin="0"
                             aria-valuemax="100"></div>
                    </div>
                </div>
            </div>

            <div class="col-12 col-md-6 col-xl-3">
                <div class="stat-card">
                    <p class="stat-label">Deficiencias detectadas</p>
                    <h3 class="stat-value"><%= totalDeficiencias %></h3>
                    <p class="stat-note">Respuestas marcadas como NO CUMPLE</p>
                </div>
            </div>
        </section>

        <h5 class="section-heading">Distribución por nivel de calidad</h5>

        <section class="row g-3">
            <div class="col-6 col-lg-3">
                <div class="stat-card">
                    <p class="stat-label">Excelente</p>
                    <h3 class="stat-value">
                        <%= estadisticas.getOrDefault("nivelExcelente", 0) %>
                    </h3>
                    <span class="quality-badge quality-excelente">90–100%</span>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="stat-card">
                    <p class="stat-label">Buena</p>
                    <h3 class="stat-value">
                        <%= estadisticas.getOrDefault("nivelBuena", 0) %>
                    </h3>
                    <span class="quality-badge quality-buena">75–89.99%</span>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="stat-card">
                    <p class="stat-label">Aceptable</p>
                    <h3 class="stat-value">
                        <%= estadisticas.getOrDefault("nivelAceptable", 0) %>
                    </h3>
                    <span class="quality-badge quality-aceptable">60–74.99%</span>
                </div>
            </div>

            <div class="col-6 col-lg-3">
                <div class="stat-card">
                    <p class="stat-label">Baja</p>
                    <h3 class="stat-value">
                        <%= estadisticas.getOrDefault("nivelBaja", 0) %>
                    </h3>
                    <span class="quality-badge quality-baja">Menos de 60%</span>
                </div>
            </div>
        </section>

        <% if (detalle != null) { %>
            <h5 class="section-heading" id="detalle">
                Detalle de la evaluación #<%= detalle.getIdEvaluacion() %>
            </h5>

            <section class="content-card mb-4">
                <div class="row g-4 mb-4">
                    <div class="col-12 col-md-6 col-xl-3">
                        <p class="detail-label">Proyecto</p>
                        <div class="detail-value">
                            <%= detalle.getNombreProyecto() %>
                        </div>
                    </div>

                    <div class="col-12 col-md-6 col-xl-3">
                        <p class="detail-label">Evaluador</p>
                        <div class="detail-value">
                            <%= detalle.getNombreUsuario() %>
                        </div>
                    </div>

                    <div class="col-12 col-md-6 col-xl-3">
                        <p class="detail-label">Fecha</p>
                        <div class="detail-value">
                            <%= detalle.getFechaEvaluacion() != null
                                ? detalle.getFechaEvaluacion().format(formatoFecha)
                                : "No disponible" %>
                        </div>
                    </div>

                    <div class="col-12 col-md-6 col-xl-3">
                        <p class="detail-label">Cumplimiento y nivel</p>
                        <div class="detail-value">
                            <%= String.format(java.util.Locale.US, "%.2f",
                                detalle.getPorcentajeCumplimiento()) %>%
                            · <%= detalle.getNivelCalidad() %>
                        </div>
                    </div>
                </div>

                <h6 class="mb-3">Resultados por criterio</h6>

                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Criterio</th>
                                <th>Categoría</th>
                                <th>Resultado</th>
                                <th>Observación</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (respuestasDetalle.isEmpty()) { %>
                                <tr>
                                    <td colspan="5" class="text-center py-4">
                                        No se encontraron respuestas.
                                    </td>
                                </tr>
                            <% } else {
                                for (Map<String, Object> respuesta : respuestasDetalle) {
                                    String cumplimiento =
                                        String.valueOf(respuesta.get("cumplimiento"));

                                    String claseResultado =
                                        "CUMPLE".equals(cumplimiento)
                                            ? "quality-excelente"
                                            : "PARCIAL".equals(cumplimiento)
                                                ? "quality-aceptable"
                                                : "NO_CUMPLE".equals(cumplimiento)
                                                    ? "quality-baja"
                                                    : "quality-default";
                            %>
                                <tr>
                                    <td><%= respuesta.get("codigo") %></td>
                                    <td>
                                        <strong><%= respuesta.get("nombreCriterio") %></strong>
                                        <div class="small text-secondary">
                                            <%= respuesta.get("descripcion") %>
                                        </div>
                                    </td>
                                    <td><%= respuesta.get("categoria") %></td>
                                    <td>
                                        <span class="quality-badge <%= claseResultado %>">
                                            <%= cumplimiento.replace("_", " ") %>
                                        </span>
                                    </td>
                                    <td>
                                        <%= respuesta.get("observacion") == null
                                            || String.valueOf(respuesta.get("observacion")).isBlank()
                                            ? "Sin observaciones"
                                            : respuesta.get("observacion") %>
                                    </td>
                                </tr>
                            <%  }
                               }
                            %>
                        </tbody>
                    </table>
                </div>

                <a class="btn btn-outline-secondary mt-3"
                   href="${pageContext.request.contextPath}/evaluaciones?vista=admin">
                    Cerrar detalle
                </a>
            </section>
        <% } %>

        <h5 class="section-heading">Historial de evaluaciones</h5>

        <section class="content-card">
            <div class="d-flex flex-wrap justify-content-between
                        align-items-center gap-2 mb-3">
                <div>
                    <h5 class="mb-1">Evaluaciones registradas</h5>
                    <p class="text-secondary small mb-0">
                        Selecciona una evaluación para consultar sus resultados.
                    </p>
                </div>

                <input type="search"
                       id="buscarEvaluacion"
                       class="form-control"
                       style="max-width: 300px"
                       placeholder="Buscar proyecto o evaluador..."
                       aria-label="Buscar evaluación">
            </div>

            <div class="table-responsive">
                <table class="table table-hover" id="tablaEvaluaciones">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Proyecto</th>
                            <th>Evaluador</th>
                            <th>Fecha</th>
                            <th>Cumplimiento</th>
                            <th>Nivel</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (evaluaciones.isEmpty()) { %>
                            <tr>
                                <td colspan="7">
                                    <div class="empty-state">
                                        <h6>No hay evaluaciones registradas</h6>
                                        <p class="mb-0">
                                            Cuando los usuarios completen
                                            evaluaciones, aparecerán aquí.
                                        </p>
                                    </div>
                                </td>
                            </tr>
                        <% } else {
                            for (Evaluacion e : evaluaciones) {
                                String nivel = e.getNivelCalidad() == null
                                    ? "" : e.getNivelCalidad();

                                String claseNivel =
                                    "EXCELENTE".equalsIgnoreCase(nivel)
                                        ? "quality-excelente"
                                        : "BUENA".equalsIgnoreCase(nivel)
                                            ? "quality-buena"
                                            : "ACEPTABLE".equalsIgnoreCase(nivel)
                                                ? "quality-aceptable"
                                                : "BAJA".equalsIgnoreCase(nivel)
                                                    ? "quality-baja"
                                                    : "quality-default";

                                String fecha = e.getFechaEvaluacion() != null
                                    ? e.getFechaEvaluacion().format(formatoFecha)
                                    : "No disponible";
                        %>
                            <tr class="fila-evaluacion">
                                <td>#<%= e.getIdEvaluacion() %></td>
                                <td class="col-proyecto">
                                    <strong><%= e.getNombreProyecto() %></strong>
                                </td>
                                <td class="col-evaluador">
                                    <%= e.getNombreUsuario() %>
                                </td>
                                <td><%= fecha %></td>
                                <td>
                                    <strong>
                                        <%= String.format(java.util.Locale.US, "%.2f",
                                            e.getPorcentajeCumplimiento()) %>%
                                    </strong>
                                </td>
                                <td>
                                    <span class="quality-badge <%= claseNivel %>">
                                        <%= nivel %>
                                    </span>
                                </td>
                                <td>
                                    <a class="btn-details"
                                       href="${pageContext.request.contextPath}/evaluaciones?vista=admin&amp;detalle=<%= e.getIdEvaluacion() %>#detalle">
                                        Ver detalle
                                    </a>
                                </td>
                            </tr>
                        <%  }
                           }
                        %>
                    </tbody>
                </table>
            </div>

            <p class="small text-secondary mt-3 mb-0">
                Registros mostrados: <span id="contadorEvaluaciones"><%= evaluaciones.size() %></span>
            </p>
        </section>

        <footer class="dashboard-footer">
            SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
        </footer>
    </main>
</div>

<script>
    const campoBusqueda = document.getElementById("buscarEvaluacion");
    const filas = document.querySelectorAll(
        "#tablaEvaluaciones tbody tr.fila-evaluacion"
    );
    const contador = document.getElementById("contadorEvaluaciones");

    if (campoBusqueda) {
        campoBusqueda.addEventListener("input", function () {
            const termino = this.value.toLocaleLowerCase("es");
            let visibles = 0;

            filas.forEach(function (fila) {
                const texto = fila.textContent.toLocaleLowerCase("es");
                const coincide = texto.includes(termino);

                fila.style.display = coincide ? "" : "none";

                if (coincide) {
                    visibles++;
                }
            });

            contador.textContent = visibles;
        });
    }
</script>

<script src="${pageContext.request.contextPath}/bootstrap/bootstrap.bundle.min.js"></script>
</body>
</html>