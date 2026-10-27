<%@page contentType="text/html" pageEncoding="UTF-8"
    import="com.poo2.modelo.CuentaUsuario"
    import="java.util.List"
    import="java.util.Map"%>

<%!
    private String escaparHTML(Object valor) {
        if (valor == null) return "";

        return valor.toString()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
%>

<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String rolSesion = (String) session.getAttribute("rol");

    if (rolSesion == null || !"ADMIN".equalsIgnoreCase(rolSesion)) {
        response.sendRedirect(
            request.getContextPath() + "/acceso-denegado.jsp");
        return;
    }

    List<CuentaUsuario> usuarios =
        (List<CuentaUsuario>) request.getAttribute("usuarios");

    Map<Integer, String> roles =
        (Map<Integer, String>) request.getAttribute("roles");

    if (usuarios == null || roles == null) {
        response.sendRedirect(request.getContextPath() + "/admin/usuarios");
        return;
    }

    String mensaje = (String) request.getAttribute("mensajeUsuarios");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Gestión de usuarios | SoftQuality</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/bootstrap/bootstrap.min.css">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            background: #f4f7fc;
            color: #172554;
            font-family: Arial, sans-serif;
        }

        .pagina {
            max-width: 1500px;
            margin: auto;
            padding: 30px;
        }

        .encabezado {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            margin-bottom: 25px;
        }

        .encabezado h1 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .subtitulo {
            color: #64748b;
            margin: 0;
        }

        .panel {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 15px;
            padding: 24px;
            box-shadow: 0 4px 16px rgba(23, 37, 84, 0.05);
            margin-bottom: 22px;
        }

        .resumen {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 16px;
            margin-bottom: 22px;
        }

        .resumen-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 13px;
            padding: 20px;
        }

        .resumen-card span {
            color: #64748b;
            font-size: 13px;
        }

        .resumen-card strong {
            display: block;
            font-size: 28px;
            margin-top: 8px;
        }

        .formulario-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
            gap: 16px;
            align-items: end;
        }

        .campo label {
            display: block;
            font-weight: 600;
            font-size: 13px;
            margin-bottom: 7px;
        }

        .campo input,
        .campo select {
            width: 100%;
            min-width: 0;
            padding: 10px 11px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            background: #fff;
            color: #172554;
        }

        .campo input:focus,
        .campo select:focus {
            outline: 2px solid #bfdbfe;
            border-color: #2563eb;
        }

        .tabla-contenedor {
            overflow-x: auto;
        }

        .tabla-usuarios {
            width: 100%;
            border-collapse: collapse;
            min-width: 1000px;
        }

        .tabla-usuarios th {
            background: #f8fafc;
            color: #475569;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: .4px;
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        .tabla-usuarios td {
            padding: 15px 12px;
            border-bottom: 1px solid #edf2f7;
            font-size: 14px;
            vertical-align: middle;
        }

        .tabla-usuarios tbody tr:hover {
            background: #f8fafc;
        }

        .etiqueta {
            display: inline-block;
            padding: 6px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            white-space: nowrap;
        }

        .rol-admin {
            background: #dbeafe;
            color: #1d4ed8;
        }

        .rol-usuario {
            background: #f1f5f9;
            color: #475569;
        }

        .estado-activo {
            background: #dcfce7;
            color: #166534;
        }

        .estado-inactivo {
            background: #fee2e2;
            color: #991b1b;
        }

        .estado-bloqueado {
            background: #ffedd5;
            color: #9a3412;
        }

        .boton {
            display: inline-block;
            border: 0;
            border-radius: 8px;
            padding: 10px 15px;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
            font-weight: 600;
        }

        .boton-volver {
            background: #172554;
            color: white;
        }

        .boton-volver:hover {
            background: #263b70;
            color: white;
        }

        .boton-primario {
            background: #2563eb;
            color: white;
        }

        .boton-primario:hover {
            background: #1d4ed8;
            color: white;
        }

        .boton-peligro {
            background: #fee2e2;
            color: #991b1b;
        }

        .boton-peligro:hover {
            background: #fecaca;
        }

        .acciones {
            min-width: 210px;
        }

        .acciones details {
            margin-bottom: 10px;
        }

        .acciones summary {
            color: #1d4ed8;
            cursor: pointer;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .form-edicion {
            min-width: 260px;
            padding: 12px;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            background: white;
        }

        .form-edicion .campo {
            margin-bottom: 12px;
        }

        .vacio {
            text-align: center;
            padding: 35px !important;
            color: #64748b;
        }

        @media (max-width: 600px) {
            .pagina {
                padding: 16px;
            }

            .panel {
                padding: 15px;
            }

            .encabezado h1 {
                font-size: 23px;
            }
        }
    </style>
</head>

<body>
<main class="pagina">

    <header class="encabezado">
        <div>
            <h1>Gestión de usuarios</h1>
            <p class="subtitulo">
                Administración de cuentas, roles y estados del sistema.
            </p>
        </div>

        <a class="boton boton-volver"
           href="${pageContext.request.contextPath}/admin/dashboard">
            Volver al panel
        </a>
    </header>

    <% if (mensaje != null && !mensaje.isBlank()) { %>
        <div class="alert alert-info" role="alert">
            <%= escaparHTML(mensaje) %>
        </div>
    <% } %>

    <section class="resumen">
        <div class="resumen-card">
            <span>Usuarios registrados</span>
            <strong><%= usuarios.size() %></strong>
        </div>

        <div class="resumen-card">
            <span>Roles disponibles</span>
            <strong><%= roles.size() %></strong>
        </div>

        <div class="resumen-card">
            <span>Cuentas activas</span>
            <strong>
                <%
                    int activas = 0;
                    for (CuentaUsuario u : usuarios) {
                        if ("ACTIVO".equalsIgnoreCase(u.getEstado())) {
                            activas++;
                        }
                    }
                %>
                <%= activas %>
            </strong>
        </div>
    </section>

    <!-- REGISTRAR USUARIO -->
    <section class="panel">
        <h2 class="h5 mb-2">Registrar nuevo usuario</h2>
        <p class="text-secondary mb-4">
            La cuenta se registrará con el rol USUARIO. Después podrás
            cambiar su rol desde las opciones de edición.
        </p>

        <form method="post"
              action="${pageContext.request.contextPath}/admin/usuarios">

            <input type="hidden" name="accion" value="crear">

            <div class="formulario-grid">
                <div class="campo">
                    <label for="nombre">Nombre completo</label>
                    <input type="text" id="nombre" name="nombre"
                           maxlength="100" required>
                </div>

                <div class="campo">
                    <label for="email">Correo electrónico</label>
                    <input type="email" id="email" name="email"
                           maxlength="150" required>
                </div>

                <div class="campo">
                    <label for="password">Contraseña inicial</label>
                    <input type="password" id="password" name="password"
                           minlength="8" autocomplete="new-password" required>
                </div>

                <div>
                    <button type="submit" class="boton boton-primario">
                        Registrar usuario
                    </button>
                </div>
            </div>
        </form>
    </section>

    <!-- LISTADO Y EDICIÓN -->
    <section class="panel">
        <h2 class="h5 mb-2">Listado de cuentas</h2>
        <p class="text-secondary mb-4">
            Consulta las cuentas, edita sus datos o cambia su estado.
        </p>

        <div class="tabla-contenedor">
            <table class="tabla-usuarios">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Correo electrónico</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th>Intentos fallidos</th>
                        <th>Último acceso</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>
                <% if (usuarios.isEmpty()) { %>
                    <tr>
                        <td colspan="8" class="vacio">
                            No hay usuarios registrados.
                        </td>
                    </tr>
                <% } else {
                    for (CuentaUsuario cuenta : usuarios) {
                        String rolNombre = cuenta.getRolNombre();
                        String estado = cuenta.getEstado();

                        boolean bloqueado = cuenta.isBloqueado();

                        String claseRol =
                            "ADMIN".equalsIgnoreCase(rolNombre)
                            ? "rol-admin" : "rol-usuario";

                        String claseEstado;
                        String textoEstado;

                        if (bloqueado) {
                            claseEstado = "estado-bloqueado";
                            textoEstado = "Bloqueado";
                        } else if ("ACTIVO".equalsIgnoreCase(estado)) {
                            claseEstado = "estado-activo";
                            textoEstado = "Activo";
                        } else {
                            claseEstado = "estado-inactivo";
                            textoEstado = "Inactivo";
                        }
                %>
                    <tr>
                        <td><%= cuenta.getIdUsuario() %></td>

                        <td>
                            <%= escaparHTML(cuenta.getNombreUsuario()) %>
                        </td>

                        <td><%= escaparHTML(cuenta.getEmail()) %></td>

                        <td>
                            <span class="etiqueta <%= claseRol %>">
                                <%= escaparHTML(rolNombre) %>
                            </span>
                        </td>

                        <td>
                            <span class="etiqueta <%= claseEstado %>">
                                <%= textoEstado %>
                            </span>
                        </td>

                        <td><%= cuenta.getIntentosFallidos() %></td>

                        <td>
                            <%= cuenta.getUltimoAcceso() == null
                                ? "Nunca"
                                : escaparHTML(cuenta.getUltimoAcceso()) %>
                        </td>

                        <td class="acciones">

                            <!-- EDITAR CUENTA -->
                            <details>
                                <summary>Editar cuenta</summary>

                                <form class="form-edicion" method="post"
                                      action="${pageContext.request.contextPath}/admin/usuarios">

                                    <input type="hidden" name="accion"
                                           value="actualizar">

                                    <input type="hidden" name="id"
                                           value="<%= cuenta.getIdUsuario() %>">

                                    <div class="campo">
                                        <label>Nombre</label>
                                        <input type="text" name="nombre"
                                               maxlength="100"
                                               value="<%= escaparHTML(cuenta.getNombreUsuario()) %>"
                                               required>
                                    </div>

                                    <div class="campo">
                                        <label>Correo electrónico</label>
                                        <input type="email" name="email"
                                               maxlength="150"
                                               value="<%= escaparHTML(cuenta.getEmail()) %>"
                                               required>
                                    </div>

                                    <div class="campo">
                                        <label>Rol</label>
                                        <select name="rolId" required>
                                            <% for (Map.Entry<Integer, String> rol
                                                    : roles.entrySet()) { %>
                                                <option
                                                    value="<%= rol.getKey() %>"
                                                    <%= cuenta.getRolId() == rol.getKey()
                                                        ? "selected" : "" %>>
                                                    <%= escaparHTML(rol.getValue()) %>
                                                </option>
                                            <% } %>
                                        </select>
                                    </div>

                                    <div class="campo">
                                        <label>Estado</label>
                                        <select name="estado" required>
                                            <option value="ACTIVO"
                                                <%= "ACTIVO".equalsIgnoreCase(estado)
                                                    ? "selected" : "" %>>
                                                ACTIVO
                                            </option>

                                            <option value="INACTIVO"
                                                <%= "INACTIVO".equalsIgnoreCase(estado)
                                                    ? "selected" : "" %>>
                                                INACTIVO
                                            </option>
                                        </select>
                                    </div>

                                    <button type="submit"
                                            class="boton boton-primario">
                                        Guardar cambios
                                    </button>
                                </form>
                            </details>

                            <!-- DESACTIVAR CUENTA -->
                            <% if ("ACTIVO".equalsIgnoreCase(estado)) { %>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/usuarios"
                                      onsubmit="return confirm('¿Deseas desactivar esta cuenta?');">

                                    <input type="hidden" name="accion"
                                           value="desactivar">

                                    <input type="hidden" name="id"
                                           value="<%= cuenta.getIdUsuario() %>">

                                    <button type="submit"
                                            class="boton boton-peligro">
                                        Desactivar
                                    </button>
                                </form>
                                           <% } %>

                            <!-- DESBLOQUEAR CUENTA -->
                            <% if (cuenta.isBloqueado()) { %>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/admin/usuarios"
                                      onsubmit="return confirm('¿Deseas desbloquear esta cuenta?');">

                                    <input type="hidden" name="accion"
                                           value="desbloquear">

                                    <input type="hidden" name="id"
                                           value="<%= cuenta.getIdUsuario() %>">

                                    <button type="submit"
                                            class="boton boton-primario">
                                        Desbloquear
                                    </button>
                                </form>
                            <% } %>
                        </td>
                    </tr>
                <%  }
                   } %>
                </tbody>
            </table>
        </div>
    </section>

    <footer class="text-secondary text-center small">
        SoftQuality Solutions S.A.C. · Sistema de evaluación MVC
    </footer>

</main>
</body>
</html>