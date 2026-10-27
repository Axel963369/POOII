
package com.poo2.controlador;

import com.poo2.servicio.AutenticacionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private final AutenticacionService autenticacion =
            AutenticacionService.getInstancia();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            boolean registrado = autenticacion.registrarUsuario(
                    nombre, email, password);

            if (registrado) {
                response.sendRedirect(
                        request.getContextPath() + "/login.jsp");
            } else {
                request.setAttribute("error",
                        "No se pudo registrar la cuenta. "
                        + "Comprueba si el correo ya está registrado.");

                request.getRequestDispatcher("/registro.jsp")
                        .forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher("/registro.jsp")
                    .forward(request, response);

        } catch (RuntimeException e) {
            getServletContext().log(
                    "Error interno durante el registro.", e);

            request.setAttribute("error",
                    "No se pudo completar el registro. "
                    + "Inténtalo más tarde.");

            request.getRequestDispatcher("/registro.jsp")
                    .forward(request, response);
        }
    }
}
