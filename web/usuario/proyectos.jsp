<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Proyectos | SoftQuality Solutions</title>

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
            margin-bottom: 30px;
            flex-wrap: wrap;
        }

        .content-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            padding: 26px;
            margin-bottom: 25px;
            box-sizing: border-box;
            box-shadow: 0 4px 18px rgba(23, 37, 84, 0.06);
        }

        .dashboard-main h1,
        .dashboard-main h2,
        .dashboard-main h3,
        .dashboard-main h4,
        .dashboard-main h5 {
            color: #172554;
        }

        .form-control,
        .form-select {
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            padding: 10px 12px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #60a5fa;
            box-shadow: 0 0 0 0.2rem rgba(59, 130, 246, 0.15);
        }

        .btn-primary {
            background: #1d4ed8;
            border-color: #1d4ed8;
            border-radius: 8px;
        }

        .btn-primary:hover {
            background: #1e40af;
            border-color: #1e40af;
        }

        .table th {
            color: #475569;
            white-space: nowrap;
            background: #f8fafc;
            font-size: 0.9rem;
        }

        .table td {
            vertical-align: middle;
            color: #334155;
        }

        .project-description {
            min-width: 180px;
            max-width: 300px;
            white-space: normal;
            overflow-wrap: anywhere;
            margin-top: 5px;
        }

        .project-actions {
            min-width: 210px;
        }

        .sidebar-footer {
            margin-top: 35px;
        }

        .section-title {
            font-weight: 700;
            margin-bottom: 5px;
        }

        .section-subtitle {
            color: #64748b;
            font-size: 0.92rem;
        }

        .project-count {
            background: #eff6ff;
            color: #1d4ed8;
            padding: 8px 13px;
            border-radius: 8px;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .dashboard-footer {
            color: #64748b;
            font-size: 0.85rem;
            margin-top: 30px;
            padding-bottom: 10px;
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

            .content-card {
                padding: 18px;
            }

            .project-actions {
                min-width: 180px;
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

            <c:choose>
                <c:when test="${esAdmin}">
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/admin/dashboard">
                        Inicio
                    </a>

                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/admin/usuarios">
                        Gestión de usuarios
                    </a>
                </c:when>

                <c:otherwise>
                    <a class="nav-link"
                       href="${pageContext.request.contextPath}/usuario/dashboard.jsp">
                        Inicio
                    </a>
                </c:otherwise>
            </c:choose>

            <a class="nav-link active"
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
                <h2 class="mb-2">
                    <c:choose>
                        <c:when test="${esAdmin}">
                            Gestión de proyectos
                        </c:when>
                        <c:otherwise>
                            Mis proyectos
                        </c:otherwise>
                    </c:choose>
                </h2>

                <p class="text-secondary mb-0">
                    Registra, consulta y administra proyectos de software.
                </p>
            </div>

            <c:choose>
                <c:when test="${esAdmin}">
                    <span class="badge text-bg-primary">ADMINISTRADOR</span>
                </c:when>
                <c:otherwise>
                    <span class="badge text-bg-success">USUARIO</span>
                </c:otherwise>
            </c:choose>
        </header>

        <c:if test="${not empty mensajeProyectos}">
            <div class="alert alert-info alert-dismissible fade show"
                 role="alert">
                <c:out value="${mensajeProyectos}"/>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="alert"
                        aria-label="Cerrar">
                </button>
            </div>
        </c:if>

        <!-- Formulario para registrar proyectos -->
        <section class="content-card">
            <h5 class="section-title">Registrar nuevo proyecto</h5>

            <p class="section-subtitle mb-4">
                Completa la información para incorporar un proyecto al sistema.
            </p>

            <form action="${pageContext.request.contextPath}/proyectos"
                  method="post">

                <input type="hidden" name="accion" value="crear">

                <div class="row g-3">

                    <div class="col-12 col-md-6">
                        <label for="nombre" class="form-label">
                            Nombre del proyecto *
                        </label>

                        <input type="text"
                               id="nombre"
                               name="nombre"
                               class="form-control"
                               maxlength="120"
                               required
                               placeholder="Ej. Sistema de biblioteca">
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="tecnologia" class="form-label">
                            Tecnología principal
                        </label>

                        <input type="text"
                               id="tecnologia"
                               name="tecnologia"
                               class="form-control"
                               maxlength="100"
                               placeholder="Ej. Java, PostgreSQL">
                    </div>

                    <div class="col-12">
                        <label for="descripcion" class="form-label">
                            Descripción
                        </label>

                        <textarea id="descripcion"
                                  name="descripcion"
                                  class="form-control"
                                  rows="3"
                                  placeholder="Describe brevemente el proyecto"></textarea>
                    </div>

                    <div class="col-12">
                        <button type="submit" class="btn btn-primary">
                            + Registrar proyecto
                        </button>
                    </div>

                </div>
            </form>
        </section>

        <!-- Tabla de proyectos -->
        <section class="content-card">

            <div class="d-flex justify-content-between align-items-center
                        flex-wrap gap-3 mb-4">

                <div>
                    <h5 class="section-title">Proyectos registrados</h5>
                    <p class="section-subtitle mb-0">
                        Consulta el estado y los datos de tus proyectos.
                    </p>
                </div>

                <span class="project-count">
                    Total: ${proyectos.size()}
                </span>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">

                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Proyecto</th>
                        <th>Tecnología</th>
                        <th>Estado</th>

                        <c:if test="${esAdmin}">
                            <th>ID usuario</th>
                        </c:if>

                        <th>Fecha de registro</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach var="proyecto" items="${proyectos}">

                        <tr>
                            <td>
                                <c:out value="${proyecto.idProyecto}"/>
                            </td>

                            <td>
                                <strong>
                                    <c:out value="${proyecto.nombre}"/>
                                </strong>

                                <div class="project-description small text-secondary">
                                    <c:out value="${proyecto.descripcion}"/>
                                </div>
                            </td>

                            <td>
                                <c:out value="${proyecto.tecnologia}"/>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${proyecto.estado eq 'REGISTRADO'}">
                                        <span class="badge text-bg-secondary">
                                            Registrado
                                        </span>
                                    </c:when>

                                    <c:when test="${proyecto.estado eq 'EN_EVALUACION'}">
                                        <span class="badge text-bg-primary">
                                            En evaluación
                                        </span>
                                    </c:when>

                                    <c:when test="${proyecto.estado eq 'EVALUADO'}">
                                        <span class="badge text-bg-success">
                                            Evaluado
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="badge text-bg-danger">
                                            Inactivo
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <c:if test="${esAdmin}">
                                <td>
                                    <c:out value="${proyecto.idUsuario}"/>
                                </td>
                            </c:if>

                            <td>
                                <c:out value="${proyecto.fechaCreacion}"/>
                            </td>

                            <td class="project-actions">

                                <c:if test="${proyecto.estado ne 'INACTIVO'}">
                                    <button type="button"
                                            class="btn btn-sm btn-outline-primary mb-1"
                                            data-bs-toggle="collapse"
                                            data-bs-target="#editar${proyecto.idProyecto}">
                                        Editar
                                    </button>
                                </c:if>

                                <c:if test="${esAdmin}">
                                    <form action="${pageContext.request.contextPath}/proyectos"
                                          method="post"
                                          class="d-inline">

                                        <input type="hidden"
                                               name="accion"
                                               value="cambiarEstado">

                                        <input type="hidden"
                                               name="idProyecto"
                                               value="${proyecto.idProyecto}">

                                        <c:choose>
                                            <c:when test="${proyecto.estado eq 'INACTIVO'}">
                                                <input type="hidden"
                                                       name="estado"
                                                       value="REGISTRADO">

                                                <button type="submit"
                                                        class="btn btn-sm btn-outline-success mb-1">
                                                    Reactivar
                                                </button>
                                            </c:when>

                                            <c:otherwise>
                                                <input type="hidden"
                                                       name="estado"
                                                       value="INACTIVO">

                                                <button type="submit"
                                                        class="btn btn-sm btn-outline-danger mb-1"
                                                        onclick="return confirm('¿Deseas desactivar este proyecto?');">
                                                    Desactivar
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </c:if>

                                <c:if test="${not esAdmin and proyecto.estado eq 'REGISTRADO'}">
                                    <form action="${pageContext.request.contextPath}/proyectos"
                                          method="post"
                                          class="d-inline">

                                        <input type="hidden"
                                               name="accion"
                                               value="cambiarEstado">

                                        <input type="hidden"
                                               name="idProyecto"
                                               value="${proyecto.idProyecto}">

                                        <input type="hidden"
                                               name="estado"
                                               value="EN_EVALUACION">

                                        <button type="submit"
                                                class="btn btn-sm btn-outline-success mb-1">
                                            Iniciar evaluación
                                        </button>
                                    </form>
                                </c:if>

                            </td>
                        </tr>

                        <!-- Formulario desplegable para editar -->
                        <c:if test="${proyecto.estado ne 'INACTIVO'}">

                            <tr class="collapse"
                                id="editar${proyecto.idProyecto}">

                                <td colspan="${esAdmin ? 7 : 6}">
                                    <form action="${pageContext.request.contextPath}/proyectos"
                                          method="post"
                                          class="border rounded-3 p-3 p-md-4 bg-light">

                                        <input type="hidden"
                                               name="accion"
                                               value="actualizar">

                                        <input type="hidden"
                                               name="idProyecto"
                                               value="${proyecto.idProyecto}">

                                        <h6 class="mb-3">Editar proyecto</h6>

                                        <div class="row g-3">

                                            <div class="col-12 col-md-4">
                                                <label class="form-label">
                                                    Nombre
                                                </label>

                                                <input type="text"
                                                       name="nombre"
                                                       class="form-control"
                                                       maxlength="120"
                                                       required
                                                       value="<c:out value='${proyecto.nombre}'/>">
                                            </div>

                                            <div class="col-12 col-md-4">
                                                <label class="form-label">
                                                    Tecnología
                                                </label>

                                                <input type="text"
                                                       name="tecnologia"
                                                       class="form-control"
                                                       maxlength="100"
                                                       value="<c:out value='${proyecto.tecnologia}'/>">
                                            </div>

                                            <div class="col-12 col-md-4">
                                                <label class="form-label">
                                                    Descripción
                                                </label>

                                                <input type="text"
                                                       name="descripcion"
                                                       class="form-control"
                                                       value="<c:out value='${proyecto.descripcion}'/>">
                                            </div>

                                            <div class="col-12">
                                                <button type="submit"
                                                        class="btn btn-primary btn-sm">
                                                    Guardar cambios
                                                </button>
                                            </div>

                                        </div>
                                    </form>
                                </td>
                            </tr>

                        </c:if>

                    </c:forEach>

                    <c:if test="${empty proyectos}">
                        <tr>
                            <td colspan="${esAdmin ? 7 : 6}"
                                class="text-center text-secondary py-5">
                                <div class="mb-2">
                                    <strong>No hay proyectos registrados</strong>
                                </div>

                                <span class="small">
                                    Puedes crear tu primer proyecto utilizando
                                    el formulario superior.
                                </span>
                            </td>
                        </tr>
                    </c:if>

                    </tbody>
                </table>
            </div>
        </section>

        <footer class="dashboard-footer">
            SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
        </footer>

    </main>
</div>

<script src="${pageContext.request.contextPath}/bootstrap/bootstrap.bundle.min.js"></script>

</body>
</html>