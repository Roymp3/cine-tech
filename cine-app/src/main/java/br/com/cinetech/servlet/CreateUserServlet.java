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

        String nm_pessoa = request.getParameter("name");
        String nm_usuario = request.getParameter("user");
        String ds_senha = request.getParameter("senha");
        String nr_telefone = request.getParameter("telefone");
        String ds_email = request.getParameter("email");

        User user = new User(nm_pessoa, nm_usuario, ds_senha, nr_telefone, ds_email);

        UserDao userdao = new UserDao();
        userdao.CreateUser(user);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("paginaCriarConta.html");
    }
}
