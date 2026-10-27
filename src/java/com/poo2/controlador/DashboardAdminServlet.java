
package com.poo2.controlador;

import com.poo2.repository.UsuarioRepository;
import com.poo2.repository.UsuarioRepositoryPostgreSQL;
import com.poo2.repository.ProyectoRepository;
import com.poo2.repository.ProyectoRepositoryPostgreSQL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class DashboardAdminServlet extends HttpServlet {
    
    private final UsuarioRepository usuarioRepository =
            new UsuarioRepositoryPostgreSQL();
    private final ProyectoRepository proyectoRepository =
        new ProyectoRepositoryPostgreSQL();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Verificar que el usuario haya iniciado sesión.
        if (session == null
                || session.getAttribute("usuario") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        // Verificar que tenga el rol de administrador.
        String rol = (String) session.getAttribute("rol");

        if (rol == null || !"ADMIN".equalsIgnoreCase(rol)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/acceso-denegado.jsp");
            return;
        }

        try {
            // Consultar las estadísticas en PostgreSQL.
            Map<String, Integer> estadisticas =
                    usuarioRepository.obtenerEstadisticasUsuarios();

            request.setAttribute(
                    "totalUsuarios",
                    estadisticas.getOrDefault("total", 0));

            request.setAttribute(
                    "usuariosActivos",
                    estadisticas.getOrDefault("activos", 0));

            request.setAttribute(
                    "cuentasBloqueadas",
                    estadisticas.getOrDefault("bloqueados", 0));

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al cargar las estadísticas del dashboard.", e);

            request.setAttribute(
                    "errorEstadisticas",
                    "No fue posible cargar las estadísticas. "
                    + "Inténtalo nuevamente más tarde.");
        }
        
        try {
            int totalProyectos =
                    proyectoRepository.listarTodos().size();
            request.setAttribute("totalProyectos", totalProyectos);
        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error al cargar el total de proyectos.", e);
            request.setAttribute("totalProyectos", null);
        }

        // Enviar los datos a la vista.
        request.getRequestDispatcher(
                "/admin/dashboard.jsp")
                .forward(request, response);
    }
}
