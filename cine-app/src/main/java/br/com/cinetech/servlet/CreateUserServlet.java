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
        String ds_senha =   request.getParameter("senha");
        String nr_telefone = request.getParameter("telefone");
        String ds_email = request.getParameter("email");

        if (nm_pessoa == null || nm_usuario == null || ds_senha == null || nr_telefone == null || ds_email == null) {
            response.getWriter().write("Por favor, preencha todos os campos.");
            return;

        }else if(ds_senha.length() < 8) {
            response.getWriter().write("A senha deve ter no mínimo 8 caracteres.");
            return;
        }

        User user = new User(nm_pessoa, nm_usuario, ds_senha, nr_telefone, ds_email);

        if (userdao.VerifyUser(user)) {
            response.getWriter().write("Nome de usuário já está em uso.");
        } else if (userdao.verifyEmail(user)) {
            response.getWriter().write("E-mail já está em uso.");
        } else if (userdao.verifyNumber(user)) {
            response.getWriter().write("Número de telefone já está em uso.");
        } else {
            userdao.CreateUser(user);
            response.getWriter().write("Usuário cadastrado com sucesso!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("paginaCriarConta.html");
    }
}