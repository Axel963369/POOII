<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.poo2.modelo.Usuario"%>
<%@page import="com.poo2.modelo.Proyecto"%>
<%@page import="com.poo2.modelo.Criterio"%>

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

    Usuario usuarioSesion =
        (Usuario) session.getAttribute("usuario");

    String nombreUsuario = "Usuario";

    if (usuarioSesion != null && usuarioSesion.getNombre() != null) {
        nombreUsuario = usuarioSesion.getNombre();
    }

    List<Proyecto> proyectos =
        (List<Proyecto>) request.getAttribute("proyectos");

    List<Criterio> criterios =
        (List<Criterio>) request.getAttribute("criterios");

    String errorCriterios =
        (String) request.getAttribute("errorCriterios");

    String mensajeEvaluacion =
        (String) session.getAttribute("mensajeEvaluacion");

    if (mensajeEvaluacion != null) {
        session.removeAttribute("mensajeEvaluacion");
    }

    if (proyectos == null) {
        proyectos = java.util.Collections.emptyList();
    }

    if (criterios == null) {
        criterios = java.util.Collections.emptyList();
    }

    String[] categorias = {"MODEL", "VIEW", "CONTROLLER", "GENERAL"};
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Evaluaciones MVC | SoftQuality Solutions</title>

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

        .brand-title {
            font-size: 23px;
            font-weight: bold;
            margin-bottom: 8px;
        }

        .sidebar-subtitle {
            color: #bfdbfe;
            font-size: 14px;
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

        .sidebar-footer {
            margin-top: 35px;
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

        .dashboard-main h1,
        .dashboard-main h2,
        .dashboard-main h3,
        .dashboard-main h4,
        .dashboard-main h5 {
            color: #172554;
        }

        .welcome-card {
            width: 100%;
            box-sizing: border-box;
            background: white;
            padding: 28px;
            border-radius: 16px;
            margin: 0 0 25px;
            box-shadow: 0 4px 18px rgba(23, 37, 84, 0.06);
            border: 1px solid #e2e8f0;
        }

        .welcome-card p {
            color: #64748b;
            line-height: 1.7;
            margin-bottom: 0;
        }

        .content-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            padding: 24px;
            margin-bottom: 25px;
            box-shadow: 0 4px 14px rgba(23, 37, 84, 0.04);
        }

        .section-description {
            color: #64748b;
            line-height: 1.6;
            margin-bottom: 20px;
        }

        .project-select {
            max-width: 650px;
        }

        .category-heading {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 30px 0 16px;
        }

        .category-label {
            display: inline-block;
            padding: 7px 12px;
            border-radius: 8px;
            background: #dbeafe;
            color: #1d4ed8;
            font-size: 12px;
            font-weight: bold;
            letter-spacing: 0.4px;
        }

        .criterion-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 15px;
            transition: box-shadow 0.2s;
        }

        .criterion-card:hover {
            box-shadow: 0 5px 15px rgba(23, 37, 84, 0.06);
        }

        .criterion-code {
            color: #2563eb;
            font-size: 13px;
            font-weight: bold;
            margin-bottom: 7px;
        }

        .criterion-description {
            color: #64748b;
            line-height: 1.6;
            margin: 8px 0 18px;
        }

        .criterion-weight {
            color: #64748b;
            font-size: 12px;
        }

        .response-options {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 10px;
        }

        .response-option {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 9px;
            padding: 12px;
            cursor: pointer;
            color: #334155;
            font-size: 13px;
        }

        .response-option:hover {
            background: #eff6ff;
            border-color: #93c5fd;
        }

        .response-option input {
            margin-top: 3px;
            flex-shrink: 0;
        }

        .notice {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            color: #1e40af;
            padding: 16px;
            border-radius: 10px;
            line-height: 1.6;
        }

        .empty-state {
            padding: 30px 20px;
            text-align: center;
            background: #f8fafc;
            border: 1px dashed #cbd5e1;
            border-radius: 12px;
            color: #64748b;
        }

        .observation-field {
            margin-top: 18px;
        }

        .observation-field label {
            color: #334155;
            font-weight: 600;
            font-size: 13px;
            margin-bottom: 8px;
        }

        .form-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: center;
        }

        .dashboard-footer {
            margin-top: 35px;
            padding: 20px 0;
            color: #94a3b8;
            font-size: 13px;
            text-align: center;
        }

        @media (max-width: 1000px) {
            .response-options {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
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

            .response-options {
                grid-template-columns: 1fr;
            }

            .content-card,
            .welcome-card {
                padding: 20px;
            }
        }
    </style>
</head>

<body class="dashboard-body">

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

            <a class="nav-link active"
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
                <h2>Evaluaciones MVC</h2>
                <p class="text-secondary mb-0">
                    Evaluación de buenas prácticas de arquitectura de software
                </p>
            </div>

            <span class="badge text-bg-success">
                <%= "ADMIN".equalsIgnoreCase(rol)
                    ? "ADMINISTRADOR" : "USUARIO" %>
            </span>
        </header>

        <% if (mensajeEvaluacion != null) { %>
            <div class="alert alert-success" role="alert">
                <strong>¡Evaluación registrada!</strong><br>
                <%= mensajeEvaluacion %>
            </div>
        <% } %>

        <% if (errorCriterios != null) { %>
            <div class="alert alert-danger" role="alert">
                <%= errorCriterios %>
            </div>
        <% } %>

        <section class="welcome-card">
            <h3>Evaluación de la calidad arquitectónica</h3>
            <p class="mt-2">
                Bienvenido, <strong><%= nombreUsuario %></strong>.
                Selecciona un proyecto y revisa los criterios de buenas
                prácticas de Modelo, Vista y Controlador para identificar
                oportunidades de mejora en su arquitectura.
            </p>
        </section>

        <% if (proyectos.isEmpty()) { %>

            <section class="content-card">
                <div class="empty-state">
                    <h5>No tienes proyectos disponibles</h5>
                    <p>
                        Para realizar una evaluación, primero registra
                        un proyecto de software.
                    </p>
                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/proyectos">
                        Ir a mis proyectos
                    </a>
                </div>
            </section>

        <% } else if (criterios.isEmpty()) { %>

            <section class="content-card">
                <div class="empty-state">
                    <h5>No hay criterios disponibles</h5>
                    <p>
                        No se encontraron criterios activos para evaluar.
                        Comprueba que existan criterios registrados en el sistema.
                    </p>
                </div>
            </section>

        <% } else { %>

            <form method="post"
                  action="${pageContext.request.contextPath}/evaluaciones"
                  id="formEvaluacion">

                <section class="content-card">
                    <h4>1. Seleccionar proyecto</h4>

                    <p class="section-description">
                        Elige el proyecto sobre el que deseas realizar
                        la evaluación. Solo se muestran los proyectos
                        asociados a tu cuenta.
                    </p>

                    <label for="proyectoSeleccionado"
                           class="form-label fw-semibold">
                        Proyecto que se evaluará
                    </label>

                    <select id="proyectoSeleccionado"
                            name="idProyecto"
                            class="form-select project-select"
                            required>
                        <option value="">Selecciona un proyecto...</option>

                        <% for (Proyecto proyecto : proyectos) { %>
                            <option value="<%= proyecto.getIdProyecto() %>">
                                <%= proyecto.getNombre() %>
                            </option>
                        <% } %>
                    </select>
                </section>

                <section class="content-card">
                    <h4>2. Criterios de buenas prácticas MVC</h4>

                    <p class="section-description">
                        Revisa cada criterio y selecciona una opción.
                        Debes responder todos los criterios antes de registrar
                        la evaluación.
                    </p>

                    <% for (String categoria : categorias) {
                        boolean hayCriterios = false;

                        for (Criterio c : criterios) {
                            if (categoria.equalsIgnoreCase(c.getCategoria())) {
                                hayCriterios = true;
                                break;
                            }
                        }

                        if (hayCriterios) {
                            String tituloCategoria = categoria;

                            if ("MODEL".equals(categoria)) {
                                tituloCategoria = "MODEL — Modelo";
                            } else if ("VIEW".equals(categoria)) {
                                tituloCategoria = "VIEW — Vista";
                            } else if ("CONTROLLER".equals(categoria)) {
                                tituloCategoria = "CONTROLLER — Controlador";
                            } else if ("GENERAL".equals(categoria)) {
                                tituloCategoria = "GENERAL — Arquitectura MVC";
                            }
                    %>

                        <div class="category-heading">
                            <span class="category-label">
                                <%= categoria %>
                            </span>
                            <h5 class="mb-0"><%= tituloCategoria %></h5>
                        </div>

                        <% for (Criterio criterio : criterios) {
                            if (categoria.equalsIgnoreCase(
                                    criterio.getCategoria())) {
                        %>

                            <article class="criterion-card">

                                <div class="criterion-code">
                                    Criterio <%= criterio.getCodigo() %>
                                </div>

                                <h5><%= criterio.getNombre() %></h5>

                                <p class="criterion-description">
                                    <%= criterio.getDescripcion() %>
                                </p>

                                <div class="criterion-weight mb-3">
                                    Peso: <%= criterio.getPeso() %>
                                </div>

                                <div class="response-options">

                                    <label class="response-option">
                                        <input type="radio"
                                               name="respuesta_<%= criterio.getIdCriterio() %>"
                                               value="CUMPLE"
                                               required>
                                        <span>Cumple</span>
                                    </label>

                                    <label class="response-option">
                                        <input type="radio"
                                               name="respuesta_<%= criterio.getIdCriterio() %>"
                                               value="PARCIAL"
                                               required>
                                        <span>Cumple parcialmente</span>
                                    </label>

                                    <label class="response-option">
                                        <input type="radio"
                                               name="respuesta_<%= criterio.getIdCriterio() %>"
                                               value="NO_CUMPLE"
                                               required>
                                        <span>No cumple</span>
                                    </label>

                                    <label class="response-option">
                                        <input type="radio"
                                               name="respuesta_<%= criterio.getIdCriterio() %>"
                                               value="NO_APLICA"
                                               required>
                                        <span>No aplica</span>
                                    </label>

                                </div>

                                <div class="observation-field">
                                    <label for="observacion_<%= criterio.getIdCriterio() %>"
                                           class="form-label">
                                        Observación (opcional)
                                    </label>

                                    <textarea
                                        id="observacion_<%= criterio.getIdCriterio() %>"
                                        name="observacion_<%= criterio.getIdCriterio() %>"
                                        class="form-control"
                                        rows="2"
                                        maxlength="2000"
                                        placeholder="Describe una deficiencia detectada o una oportunidad de mejora..."></textarea>
                                </div>

                            </article>

                        <%  }
                        } %>

                    <%  }
                    } %>

                </section>

                <section class="content-card">
                    <h4>3. Registrar evaluación</h4>

                    <p class="section-description">
                        Verifica tus respuestas antes de continuar.
                        El sistema calculará el porcentaje de cumplimiento
                        y el nivel de calidad arquitectónica.
                    </p>

                    <div class="notice mb-4">
                        <strong>Importante:</strong>
                        una vez registrada, la evaluación y sus respuestas
                        quedarán almacenadas en el sistema.
                    </div>

                    <div class="form-actions">
                        <button type="submit"
                                class="btn btn-primary btn-lg">
                            Registrar evaluación
                        </button>

                        <a class="btn btn-outline-secondary btn-lg"
                           href="${pageContext.request.contextPath}/proyectos">
                            Cancelar
                        </a>
                    </div>
                </section>

            </form>

        <% } %>

        <footer class="dashboard-footer">
            SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
        </footer>

    </main>
</div>

<script src="${pageContext.request.contextPath}/bootstrap/bootstrap.bundle.min.js"></script>

</body>
</html>