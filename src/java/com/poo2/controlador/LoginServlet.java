
package com.poo2.controlador;

import com.poo2.modelo.CuentaUsuario;
import com.poo2.modelo.Usuario;
import com.poo2.servicio.AutenticacionService;
import com.poo2.servicio.CuentaBloqueadaException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AutenticacionService autenticacion =
            AutenticacionService.getInstancia();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            CuentaUsuario cuenta =
                    autenticacion.autenticar(email, password);

            // Evita reutilizar el ID de sesión previo al login.
            HttpSession session = request.getSession();
            request.changeSessionId();

            // Compatibilidad con bienvenido.jsp, que espera Usuario.
            Usuario usuario = new Usuario(
                    cuenta.getIdUsuario(),
                    cuenta.getNombreUsuario(),
                    cuenta.getEmail()
            );

            // Guardar los datos del usuario en la sesión.
            session.setAttribute("usuario", usuario);
            session.setAttribute("idUsuario",
                    cuenta.getIdUsuario());
            session.setAttribute("rol",
                    cuenta.getRolNombre());

            // Redirigir según el rol de la cuenta.
            String rol = cuenta.getRolNombre();

            if (rol != null && rol.equalsIgnoreCase("ADMIN")) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/admin/dashboard"
                );

            } else if (rol != null
                    && rol.equalsIgnoreCase("USUARIO")) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/usuario/dashboard.jsp"
                );

            } else {
                // Si el rol no es reconocido, cerrar la sesión.
                session.invalidate();

                response.sendRedirect(
                        request.getContextPath()
                        + "/acceso-denegado.jsp"
                );
            }

        } catch (CuentaBloqueadaException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error interno durante el inicio de sesión.", e);

            request.setAttribute("error",
                    "No se pudo iniciar sesión. Inténtalo más tarde.");

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        }
    }
}