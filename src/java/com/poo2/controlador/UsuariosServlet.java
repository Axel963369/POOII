
package com.poo2.controlador;

import com.poo2.modelo.CuentaUsuario;
import com.poo2.repository.UsuarioRepository;
import com.poo2.repository.UsuarioRepositoryPostgreSQL;
import com.poo2.util.HashUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/usuarios")
public class UsuariosServlet extends HttpServlet{
     private final UsuarioRepository usuarioRepository =
            new UsuarioRepositoryPostgreSQL();

    private boolean esAdministrador(HttpSession session) {
        if (session == null || session.getAttribute("usuario") == null) {
            return false;
        }

        String rol = (String) session.getAttribute("rol");

        return rol != null && "ADMIN".equalsIgnoreCase(rol);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        if (!esAdministrador(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/acceso-denegado.jsp");
            return;
        }

        try {
            List<CuentaUsuario> usuarios = usuarioRepository.listar();
            Map<Integer, String> roles = usuarioRepository.listarRoles();

            request.setAttribute("usuarios", usuarios);
            request.setAttribute("roles", roles);

            Object mensaje = session.getAttribute("mensajeUsuarios");

            if (mensaje != null) {
                request.setAttribute("mensajeUsuarios", mensaje);
                session.removeAttribute("mensajeUsuarios");
            }

            request.getRequestDispatcher("/admin/usuarios.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al cargar la gestión de usuarios.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo cargar la gestión de usuarios.");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        if (!esAdministrador(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/acceso-denegado.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("crear".equals(accion)) {
                crearUsuario(request);
                session.setAttribute(
                        "mensajeUsuarios",
                        "Usuario registrado correctamente.");

            } else if ("actualizar".equals(accion)) {
                actualizarUsuario(request);
                session.setAttribute(
                        "mensajeUsuarios",
                        "Usuario actualizado correctamente.");
            } else if ("desactivar".equals(accion)) {
                desactivarUsuario(request, session);
                session.setAttribute(
                        "mensajeUsuarios",
                        "La cuenta fue desactivada correctamente.");
            } else if ("desbloquear".equals(accion)) {
                desbloquearUsuario(request);
                session.setAttribute(
                        "mensajeUsuarios",
                        "Usuario desbloqueado correctamente.");} else {
                throw new IllegalArgumentException(
                        "La operación solicitada no es válida.");
            }

        } catch (IllegalArgumentException e) {
            session.setAttribute("mensajeUsuarios", e.getMessage());

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al gestionar una cuenta de usuario.", e);

            session.setAttribute(
                    "mensajeUsuarios",
                    "No se pudo completar la operación. "
                    + "Verifica los datos e inténtalo nuevamente.");
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/usuarios");
    }

    private void crearUsuario(HttpServletRequest request) {
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (nombre == null || nombre.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.length() < 8) {

            throw new IllegalArgumentException(
                    "Completa el nombre y correo. "
                    + "La contraseña debe tener al menos 8 caracteres.");
        }

        if (!email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                    "Introduce un correo electrónico válido.");
        }

        CuentaUsuario cuenta = new CuentaUsuario();

        cuenta.setNombreUsuario(nombre.trim());
        cuenta.setEmail(email.trim());
        cuenta.setUsuarioRegistro("ADMIN");
        cuenta.setEstado("ACTIVO");

        String passwordHash = HashUtil.sha256(password);

        boolean registrado = usuarioRepository.registrar(
                cuenta, passwordHash);

        if (!registrado) {
            throw new IllegalArgumentException(
                    "No se pudo registrar la cuenta. "
                    + "Comprueba que el correo no esté registrado.");
        }
    }

    private void actualizarUsuario(HttpServletRequest request) {
        int id = obtenerId(request);

        CuentaUsuario cuenta = usuarioRepository.buscarPorId(id);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "No se encontró el usuario seleccionado.");
        }

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String rolTexto = request.getParameter("rolId");
        String estado = request.getParameter("estado");

        if (nombre == null || nombre.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || rolTexto == null || estado == null) {

            throw new IllegalArgumentException(
                    "Completa todos los campos obligatorios.");
        }

        if (!email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                    "Introduce un correo electrónico válido.");
        }

        int rolId;

        try {
            rolId = Integer.parseInt(rolTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El rol seleccionado no es válido.");
        }

        Map<Integer, String> roles = usuarioRepository.listarRoles();

        if (!roles.containsKey(rolId)) {
            throw new IllegalArgumentException(
                    "El rol seleccionado no existe.");
        }

        if (!"ACTIVO".equalsIgnoreCase(estado)
                && !"INACTIVO".equalsIgnoreCase(estado)) {
            throw new IllegalArgumentException(
                    "El estado seleccionado no es válido.");
        }

        cuenta.setNombreUsuario(nombre.trim());
        cuenta.setEmail(email.trim());
        cuenta.setRolId(rolId);
        cuenta.setEstado(estado.toUpperCase());

        boolean actualizado = usuarioRepository.actualizar(cuenta);

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No se pudo actualizar el usuario. "
                    + "Comprueba que el correo no esté duplicado.");
        }
    }

    private void desactivarUsuario(
            HttpServletRequest request,
            HttpSession session) {

        int id = obtenerId(request);

        Object idSesion = session.getAttribute("idUsuario");

        if (idSesion != null
                && Integer.parseInt(idSesion.toString()) == id) {
            throw new IllegalArgumentException(
                    "No puedes desactivar tu propia cuenta de administrador.");
        }

        CuentaUsuario cuenta = usuarioRepository.buscarPorId(id);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "No se encontró el usuario seleccionado.");
        }

        cuenta.setEstado("INACTIVO");

        boolean actualizado = usuarioRepository.actualizar(cuenta);

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No se pudo desactivar la cuenta.");
        }
    }

    private int obtenerId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El identificador del usuario no es válido.");
        }
    }
    
    private void desbloquearUsuario(HttpServletRequest request) {
    int id = obtenerId(request);

    CuentaUsuario cuenta = usuarioRepository.buscarPorId(id);

    if (cuenta == null) {
        throw new IllegalArgumentException(
            "No se encontró el usuario seleccionado."
        );
    }

    if (!cuenta.isBloqueado()) {
        throw new IllegalArgumentException(
            "La cuenta seleccionada no está bloqueada."
        );
    }

    boolean desbloqueado = usuarioRepository.desbloquear(id);

    if (!desbloqueado) {
        throw new IllegalArgumentException(
            "No se pudo desbloquear la cuenta."
        );
    }
}
}