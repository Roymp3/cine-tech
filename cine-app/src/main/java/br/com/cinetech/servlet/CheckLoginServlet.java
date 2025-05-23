package br.com.cinetech.servlet;

import br.com.cinetech.dao.UserDao;
import br.com.cinetech.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/check-login")
public class CheckLoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ds_email = req.getParameter("email");
        String ds_senha = req.getParameter("senha");

        User user = new User();
        user.setEmail(ds_email);
        user.setSenha(ds_senha);

        resp.setContentType("text/html;charset=UTF-8");

        UserDao userdao = new UserDao();
        boolean login = userdao.CheckLogin(user);

        if (login) {
            HttpSession session = req.getSession();
            session.setAttribute("logadoo", "logado");
            session.setAttribute("email", user.getEmail());

            if (ds_email != null && ds_email.contains("cinetech")) {
                session.setAttribute("admin", "adminn");
                resp.getWriter().write("<script>alert('Login realizado com sucesso! Bem-vindo, Admin!'); window.location.href='/paginaDashboardAdmin/pageDashboardAdmin.html';</script>");
            } else {
                resp.getWriter().write("<script>alert('Login realizado com sucesso!'); window.location.href='/index.html';</script>");
            }
        } else {
            resp.getWriter().write("<script>alert('Email ou senha incorretos'); window.history.back();</script>");
        }
    }
}
