package br.com.cinetech.servlet;

import br.com.cinetech.dao.UserDao;
import  br.com.cinetech.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        UserDao userdao = new UserDao();
        boolean login = userdao.CheckLogin(user);

        if(login){
            resp.getWriter().write("Login realizado com sucesso");

        }else{
            resp.getWriter().write("Email ou senha incorretos");

        }
    }
}
