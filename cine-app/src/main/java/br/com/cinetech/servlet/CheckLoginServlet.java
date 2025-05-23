package br.com.cinetech.servlet;

import br.com.cinetech.dao.UserDao;
import  br.com.cinetech.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

        String log = "deslogado";
        String adm = "adminn";


        UserDao userdao = new UserDao();
        boolean login = userdao.CheckLogin(user);

        if(login){
            HttpSession session = req.getSession();;
            resp.sendRedirect("/index.html");
            log = "logado";
            session.setAttribute("logadoo", log);
            if(ds_email.contains("cinetech")){

                session.setAttribute(("admin"), adm);
            }

        }else{
            resp.getWriter().write("Email ou senha incorretos");

        }
    }
}
