package br.com.cinetech.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/is-login")
public class IsLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null && "logado".equals(session.getAttribute("logadoo"))) {
            resp.sendRedirect("/perfil");
        } else {
            resp.sendRedirect("/PaginaDeLogin/PaginaDeLogin.html");
        }
    }
}
