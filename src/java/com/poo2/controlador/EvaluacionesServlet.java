
package com.poo2.controlador;

import com.poo2.modelo.Criterio;
import com.poo2.modelo.Proyecto;
import com.poo2.modelo.Usuario;
import com.poo2.modelo.Evaluacion;
import com.poo2.modelo.RespuestaEvaluacion;

import com.poo2.repository.ProyectoRepository;
import com.poo2.repository.ProyectoRepositoryPostgreSQL;
import com.poo2.repository.EvaluacionRepository;
import com.poo2.repository.EvaluacionRepositoryPostgreSQL;
import com.poo2.servicio.CriterioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

@WebServlet("/evaluaciones")
public class EvaluacionesServlet extends HttpServlet{
    private final CriterioService criterioService =
            new CriterioService();

    private final ProyectoRepository proyectoRepository =
            new ProyectoRepositoryPostgreSQL();

    private final EvaluacionRepository evaluacionRepository =
            new EvaluacionRepositoryPostgreSQL();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuario") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String vista = request.getParameter("vista");

        // =========================================
        // VISTA DEL ADMINISTRADOR
        // =========================================
        if ("admin".equals(vista)) {

            String rol = (String) session.getAttribute("rol");

            if (rol == null || !"ADMIN".equalsIgnoreCase(rol)) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/acceso-denegado.jsp");
                return;
            }

            try {
                // Historial completo.
                request.setAttribute(
                        "evaluaciones",
                        evaluacionRepository.listarEvaluaciones());

                // Indicadores generales.
                request.setAttribute(
                        "estadisticas",
                        evaluacionRepository.obtenerEstadisticas());

                // Si se solicitó el detalle de una evaluación.
                String idDetalle =
                        request.getParameter("detalle");

                if (idDetalle != null && !idDetalle.isBlank()) {

                    int idEvaluacion;

                    try {
                        idEvaluacion = Integer.parseInt(idDetalle);
                    } catch (NumberFormatException e) {
                        response.sendError(
                                HttpServletResponse.SC_BAD_REQUEST,
                                "El código de evaluación no es válido.");
                        return;
                    }

                    Evaluacion detalle =
                            evaluacionRepository.buscarEvaluacionPorId(
                                    idEvaluacion);

                    if (detalle == null) {
                        response.sendError(
                                HttpServletResponse.SC_NOT_FOUND,
                                "No se encontró la evaluación solicitada.");
                        return;
                    }

                    request.setAttribute(
                            "detalleEvaluacion", detalle);

                    request.setAttribute(
                            "respuestasDetalle",
                            evaluacionRepository
                                    .listarRespuestasPorEvaluacion(
                                            idEvaluacion));
                }

            } catch (RuntimeException e) {

                getServletContext().log(
                        "Error al cargar evaluaciones del administrador.",
                        e);

                request.setAttribute(
                        "errorEvaluaciones",
                        "No fue posible cargar la información "
                        + "de las evaluaciones.");
            }

            request.getRequestDispatcher(
                    "/admin/evaluaciones.jsp")
                    .forward(request, response);

            return;
        }

        // =========================================
        // FORMULARIO DE EVALUACIÓN DEL USUARIO
        // =========================================
        try {
            Integer idUsuario =
                    (Integer) session.getAttribute("idUsuario");

            if (idUsuario == null) {
                Usuario usuarioSesion =
                        (Usuario) session.getAttribute("usuario");

                idUsuario = usuarioSesion.getId();
            }

            List<Criterio> criterios =
                    criterioService.listarCriteriosActivos();

            List<Proyecto> proyectos =
                    proyectoRepository.listarPorUsuario(idUsuario);

            request.setAttribute("criterios", criterios);
            request.setAttribute("proyectos", proyectos);

        } catch (RuntimeException e) {

            getServletContext().log(
                    "Error al cargar los datos de evaluación.", e);

            request.setAttribute(
                    "errorCriterios",
                    "No fue posible cargar los datos de evaluación.");
        }

        request.getRequestDispatcher(
                "/usuario/evaluaciones.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("usuario") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            Integer idUsuario =
                    (Integer) session.getAttribute("idUsuario");

            if (idUsuario == null) {
                Usuario usuarioSesion =
                        (Usuario) session.getAttribute("usuario");

                idUsuario = usuarioSesion.getId();
            }

            // 1. Validar el proyecto seleccionado.
            int idProyecto;

            try {
                idProyecto = Integer.parseInt(
                        request.getParameter("idProyecto"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Selecciona un proyecto válido.");
            }

            Proyecto proyectoSeleccionado = null;

            for (Proyecto proyecto :
                    proyectoRepository.listarPorUsuario(idUsuario)) {

                if (proyecto.getIdProyecto() == idProyecto) {
                    proyectoSeleccionado = proyecto;
                    break;
                }
            }

            if (proyectoSeleccionado == null) {
                throw new IllegalArgumentException(
                        "El proyecto seleccionado no existe "
                        + "o no te pertenece.");
            }

            // 2. Obtener los criterios activos.
            List<Criterio> criterios =
                    criterioService.listarCriteriosActivos();

            if (criterios == null || criterios.isEmpty()) {
                throw new IllegalArgumentException(
                        "No hay criterios activos para evaluar.");
            }

            // 3. Preparar las respuestas.
            List<RespuestaEvaluacion> respuestas =
                    new ArrayList<>();

            Set<Integer> idsProcesados = new HashSet<>();

            for (Criterio criterio : criterios) {

                String nombreCampo =
                        "respuesta_" + criterio.getIdCriterio();

                String cumplimiento =
                        request.getParameter(nombreCampo);

                if (cumplimiento == null
                        || !cumplimiento.matches(
                            "CUMPLE|PARCIAL|NO_CUMPLE|NO_APLICA")) {

                    throw new IllegalArgumentException(
                            "Debes responder todos los criterios.");
                }

                if (!idsProcesados.add(
                        criterio.getIdCriterio())) {

                    throw new IllegalArgumentException(
                            "Se detectó un criterio duplicado.");
                }

                String observacion = request.getParameter(
                        "observacion_"
                        + criterio.getIdCriterio());

                respuestas.add(new RespuestaEvaluacion(
                        0,
                        criterio.getIdCriterio(),
                        cumplimiento,
                        observacion
                ));
            }

            // 4. Registrar la evaluación.
            Evaluacion evaluacion =
                    new Evaluacion(idProyecto, idUsuario);

            int idEvaluacion =
                    evaluacionRepository.registrarEvaluacion(
                            evaluacion, respuestas);

            // 5. Mostrar mensaje y regresar al formulario.
            session.setAttribute(
                    "mensajeEvaluacion",
                    "Evaluación registrada correctamente. Código: "
                    + idEvaluacion);

            response.sendRedirect(
                    request.getContextPath() + "/evaluaciones");

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "errorCriterios", e.getMessage());

            cargarDatosFormulario(request, session);

            request.getRequestDispatcher(
                    "/usuario/evaluaciones.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {

            getServletContext().log(
                    "Error al registrar la evaluación.", e);

            request.setAttribute(
                    "errorCriterios",
                    "No se pudo guardar la evaluación. "
                    + "Revisa los datos e inténtalo nuevamente.");

            cargarDatosFormulario(request, session);

            request.getRequestDispatcher(
                    "/usuario/evaluaciones.jsp")
                    .forward(request, response);
        }
    }

    private void cargarDatosFormulario(
            HttpServletRequest request,
            HttpSession session) throws ServletException {

        Integer idUsuario =
                (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            Usuario usuarioSesion =
                    (Usuario) session.getAttribute("usuario");

            idUsuario = usuarioSesion.getId();
        }

        request.setAttribute(
                "criterios",
                criterioService.listarCriteriosActivos());

        request.setAttribute(
                "proyectos",
                proyectoRepository.listarPorUsuario(idUsuario));
    }
}
