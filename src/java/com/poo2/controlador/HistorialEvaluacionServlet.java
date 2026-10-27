
package com.poo2.controlador;
import com.poo2.modelo.Usuario;
import com.poo2.modelo.Evaluacion;
import com.poo2.repository.HistorialEvaluacionRepository;
import com.poo2.repository.HistorialEvaluacionRepositoryPostgreSQL;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/historial-evaluaciones")
public class HistorialEvaluacionServlet extends HttpServlet {
    private final HistorialEvaluacionRepository historialRepository =
            new HistorialEvaluacionRepositoryPostgreSQL();

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

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            Object usuarioSesion = session.getAttribute("usuario");

            if (usuarioSesion instanceof Usuario usuario) {
                idUsuario = usuario.getId();
            }
        }

        if (idUsuario == null) {
            session.invalidate();
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Evaluacion> evaluaciones =
                    historialRepository.listarPorUsuario(idUsuario);

            request.setAttribute("evaluaciones", evaluaciones);

            request.getRequestDispatcher("/usuario/resultados.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al cargar el historial de evaluaciones.", e);

            request.setAttribute(
                    "errorResultados",
                    "No fue posible cargar los resultados. Inténtalo nuevamente.");

            request.setAttribute(
                    "evaluaciones",
                    java.util.Collections.emptyList());

            request.getRequestDispatcher("/usuario/resultados.jsp")
                    .forward(request, response);
        }
    }
}
