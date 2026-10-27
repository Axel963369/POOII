
package com.poo2.controlador;

import com.poo2.modelo.Proyecto;
import com.poo2.repository.ProyectoRepository;
import com.poo2.repository.ProyectoRepositoryPostgreSQL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/proyectos")
public class ProyectosServlet extends HttpServlet {
    private final ProyectoRepository proyectoRepository =
            new ProyectoRepositoryPostgreSQL();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Comprobar que exista una sesión autenticada.
        if (session == null
                || session.getAttribute("usuario") == null
                || session.getAttribute("idUsuario") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");
        int idUsuario = (Integer) session.getAttribute("idUsuario");

        if (!esUsuarioValido(rol)) {
            response.sendRedirect(
                    request.getContextPath() + "/acceso-denegado.jsp");
            return;
        }

        try {
            List<Proyecto> proyectos;

            if ("ADMIN".equalsIgnoreCase(rol)) {
                proyectos = proyectoRepository.listarTodos();
            } else {
                proyectos = proyectoRepository.listarPorUsuario(idUsuario);
            }

            request.setAttribute("proyectos", proyectos);
            request.setAttribute("esAdmin",
                    "ADMIN".equalsIgnoreCase(rol));

            String mensaje = (String)
                    session.getAttribute("mensajeProyectos");

            if (mensaje != null) {
                request.setAttribute("mensajeProyectos", mensaje);
                session.removeAttribute("mensajeProyectos");
            }

            String vista;
            if ("ADMIN".equalsIgnoreCase(rol)) {
                vista = "/admin/proyectos.jsp";
            } else {
                vista = "/usuario/proyectos.jsp";
            }
            request.getRequestDispatcher(vista)
                    .forward(request, response);
        
        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al cargar los proyectos.", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible cargar los proyectos.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuario") == null
                || session.getAttribute("idUsuario") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");

        if (!esUsuarioValido(rol)) {
            response.sendRedirect(
                    request.getContextPath() + "/acceso-denegado.jsp");
            return;
        }

        int idUsuario = (Integer) session.getAttribute("idUsuario");
        String accion = request.getParameter("accion");

        try {
            if (accion == null) {
                throw new IllegalArgumentException(
                        "No se especificó una operación.");
            }

            switch (accion) {
                case "crear" ->
                    crearProyecto(request, idUsuario, session);

                case "actualizar" ->
                    actualizarProyecto(request, idUsuario, rol, session);

                case "cambiarEstado" ->
                    cambiarEstado(request, idUsuario, rol, session);

                default ->
                    throw new IllegalArgumentException(
                            "La operación solicitada no es válida.");
            }

        } catch (IllegalArgumentException e) {
            session.setAttribute("mensajeProyectos", e.getMessage());

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al procesar una operación de proyectos.", e);

            session.setAttribute(
                    "mensajeProyectos",
                    "Ocurrió un error al procesar la operación.");
        }

        response.sendRedirect(request.getContextPath() + "/proyectos");
    }

    private void crearProyecto(HttpServletRequest request,
                              int idUsuario,
                              HttpSession session) {

        String nombre = limpiar(request.getParameter("nombre"));
        String descripcion = limpiar(
                request.getParameter("descripcion"));
        String tecnologia = limpiar(
                request.getParameter("tecnologia"));

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del proyecto es obligatorio.");
        }

        if (nombre.length() > 120) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 120 caracteres.");
        }

        if (tecnologia.length() > 100) {
            throw new IllegalArgumentException(
                    "La tecnología no puede superar los 100 caracteres.");
        }

        Proyecto proyecto = new Proyecto(
                nombre, descripcion, tecnologia, idUsuario);

        if (proyectoRepository.registrar(proyecto)) {
            session.setAttribute(
                    "mensajeProyectos",
                    "Proyecto registrado correctamente.");
        } else {
            throw new IllegalStateException(
                    "No fue posible registrar el proyecto.");
        }
    }

    private void actualizarProyecto(HttpServletRequest request,
                                    int idUsuario,
                                    String rol,
                                    HttpSession session) {

        int idProyecto = obtenerIdProyecto(request);

        Proyecto existente =
                proyectoRepository.buscarPorId(idProyecto);

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El proyecto solicitado no existe.");
        }

        boolean esAdmin = "ADMIN".equalsIgnoreCase(rol);

        if (!esAdmin && existente.getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException(
                    "No tienes permiso para modificar este proyecto.");
        }
        
        if ("INACTIVO".equals(existente.getEstado())) {
            throw new IllegalArgumentException(
                    "No se puede editar un proyecto inactivo.");
        }

        String nombre = limpiar(request.getParameter("nombre"));
        String descripcion = limpiar(
                request.getParameter("descripcion"));
        String tecnologia = limpiar(
                request.getParameter("tecnologia"));

        if (nombre.isEmpty() || nombre.length() > 120) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio y admite hasta 120 caracteres.");
        }

        if (tecnologia.length() > 100) {
            throw new IllegalArgumentException(
                    "La tecnología no puede superar los 100 caracteres.");
        }

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        existente.setTecnologia(tecnologia);

        // El Repository exige el propietario para actualizar.
        // El administrador puede editar proyectos de terceros,
        // pero se conserva el ID del propietario original.
        boolean actualizado;

        if (esAdmin && existente.getIdUsuario() != idUsuario) {
            actualizado = actualizarComoAdmin(existente);
        } else {
            actualizado = proyectoRepository.actualizar(existente);
        }

        if (!actualizado) {
            throw new IllegalStateException(
                    "No fue posible actualizar el proyecto.");
        }

        session.setAttribute(
                "mensajeProyectos",
                "Proyecto actualizado correctamente.");
    }

    private boolean actualizarComoAdmin(Proyecto proyecto) {
        return proyectoRepository.actualizarComoAdmin(proyecto);
    }

    private void cambiarEstado(HttpServletRequest request,
                               int idUsuario,
                               String rol,
                               HttpSession session) {

        int idProyecto = obtenerIdProyecto(request);
        String estado = limpiar(request.getParameter("estado"));

        Proyecto existente =
                proyectoRepository.buscarPorId(idProyecto);

        if (existente == null) {
            throw new IllegalArgumentException(
                    "El proyecto solicitado no existe.");
        }

        boolean esAdmin = "ADMIN".equalsIgnoreCase(rol);

        if (!esAdmin && existente.getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException(
                    "No tienes permiso para modificar este proyecto.");
        }
        
        if (!esAdmin) {
            if ("INACTIVO".equals(existente.getEstado())) {
                throw new IllegalArgumentException(
                        "Un proyecto inactivo solo puede ser gestionado por un administrador.");
            }
            if (!"REGISTRADO".equals(existente.getEstado())
                    || !"EN_EVALUACION".equals(estado)) {
                throw new IllegalArgumentException(
                        "Solo puedes iniciar la evaluación de un proyecto registrado.");
            }
        }

        if (!proyectoRepository.cambiarEstado(idProyecto, estado)) {
            throw new IllegalStateException(
                    "No fue posible cambiar el estado del proyecto.");
        }

        session.setAttribute(
                "mensajeProyectos",
                "Estado del proyecto actualizado correctamente.");
    }

    private int obtenerIdProyecto(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(
                    request.getParameter("idProyecto"));

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "El identificador del proyecto no es válido.");
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private boolean esUsuarioValido(String rol) {
        return rol != null
                && ("ADMIN".equalsIgnoreCase(rol)
                || "USUARIO".equalsIgnoreCase(rol));
    }
}
