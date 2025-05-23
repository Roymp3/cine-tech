package br.com.cinetech.servlet;

import br.com.cinetech.dao.UserDao;
import br.com.cinetech.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String email = (String) session.getAttribute("email");
        UserDao userDao = new UserDao();
        User user = new User();
        user.setEmail(email);

        String nome = userDao.getUserName(user);


        if (nome == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado.");
            return;
        }

        // Envia o nome como atributo para o JSP
        session.setAttribute("nome", nome);


        String teste = (String) session.getAttribute("nome");

        System.out.println("Nome do usuário: " + teste);

        // Encaminha para o JSP
        req.getRequestDispatcher("/perfil.jsp").forward(req, resp);

    }
}
