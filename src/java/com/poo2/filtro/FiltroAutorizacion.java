
package com.poo2.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter({"/admin/*", "/usuario/*"})
public class FiltroAutorizacion implements Filter {

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
        // No necesitamos inicializar nada por ahora.
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        // 1. Si no inició sesión, enviar al login.
        if (session == null
                || session.getAttribute("usuario") == null) {

            res.sendRedirect(
                req.getContextPath() + "/login.jsp"
            );
            return;
        }

        // 2. Obtener el rol guardado durante el login.
        Object rolSesion = session.getAttribute("rol");

        String rol = rolSesion == null
                ? ""
                : rolSesion.toString().toUpperCase();

        String ruta = req.getServletPath();

        // 3. ADMIN puede entrar a ambas áreas.
        if ("ADMIN".equals(rol)) {
            chain.doFilter(request, response);
            return;
        }

        // 4. USUARIO solo puede entrar a su propia área.
        if ("USUARIO".equals(rol)
                && ruta.startsWith("/usuario/")) {

            chain.doFilter(request, response);
            return;
        }

        // 5. Si no tiene permiso, denegar el acceso.
        res.sendRedirect(
            req.getContextPath() + "/acceso-denegado.jsp"
        );
    }

    @Override
    public void destroy() {
        // No necesitamos liberar recursos por ahora.
    }
}