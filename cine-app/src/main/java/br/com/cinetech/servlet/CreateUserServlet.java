package br.com.cinetech.servlet;

import br.com.cinetech.dao.UserDao;
import br.com.cinetech.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

    @WebServlet("/create-user")
public class CreateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userdao = new UserDao();
        String nm_pessoa = request.getParameter("name");
        String nm_usuario = request.getParameter("user");
        String ds_senha = request.getParameter("senha");
        String nr_telefone = request.getParameter("telefone");
        String ds_email = request.getParameter("email");

        response.setContentType("text/html;charset=UTF-8");

        if (nm_pessoa == null || nm_usuario == null || ds_senha == null || nr_telefone == null || ds_email == null) {
            response.getWriter().write("<script>alert('Por favor, preencha todos os campos.'); window.history.back();</script>");
            return;

        } else if (ds_senha.length() < 8) {
            response.getWriter().write("<script>alert('A senha deve ter no mínimo 8 caracteres.'); window.history.back();</script>");
            return;
        }

        User user = new User(nm_pessoa, nm_usuario, ds_senha, nr_telefone, ds_email);

        if (userdao.VerifyUser(user)) {
            response.getWriter().write("<script>alert('Nome de usuário já está em uso.'); window.history.back();</script>");
        } else if (userdao.verifyEmail(user)) {
            response.getWriter().write("<script>alert('E-mail já está em uso.'); window.history.back();</script>");
        } else if (userdao.verifyNumber(user)) {
            response.getWriter().write("<script>alert('Número de telefone já está em uso.'); window.history.back();</script>");
        } else {
            userdao.CreateUser(user);
            response.getWriter().write("<script>alert('Usuário cadastrado com sucesso!'); window.location.href='/PaginaDeLogin/PaginaDeLogin.html';</script>");
        }
    }

        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("paginaCriarConta.html");
    }
}