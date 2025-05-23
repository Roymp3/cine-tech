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
        HttpSession session = req.getSession(false); // pega a sessão se existir, não cria uma nova

        if (session != null && "logado".equals(session.getAttribute("logadoo"))) {
            // Está logado, manda pra página de usuário
            resp.sendRedirect("/PaginaPerfil/usuario.html");
        } else {
            // Não está logado, manda pra página de login
            resp.sendRedirect("/PaginaDeLogin/PaginaDeLogin.html");
        }
    }
}
