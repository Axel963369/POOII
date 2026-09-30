
package com.poo2.controlador;

import com.poo2.dao.UsuarioDAO;
import com.poo2.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setEmail(email);

        UsuarioDAO dao = new UsuarioDAO();

        boolean registrado = dao.registrar(usuario, password);

        if (registrado) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp"
            );

        } else {

            request.setAttribute(
                    "error",
                    "El correo ya existe u ocurrió un error."
            );

            request.getRequestDispatcher("/registro.jsp")
                    .forward(request, response);
        }
    }
}
