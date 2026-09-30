
package com.poo2.controlador;

import com.poo2.dao.UsuarioDAO;
import com.poo2.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();

        Usuario usuario = dao.validar(email, password);

        if (usuario != null) {

            HttpSession session = request.getSession();

            session.setAttribute("usuario", usuario);

            response.sendRedirect(
                    request.getContextPath() + "/bienvenido.jsp"
            );

        } else {

            request.setAttribute(
                    "error",
                    "Correo o contraseña incorrectos."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        }
    }
}
