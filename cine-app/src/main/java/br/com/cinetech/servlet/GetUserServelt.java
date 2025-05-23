package br.com.cinetech.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/get-user")
public class GetUserServelt extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false); // pega sessão se existir, senão null

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (session != null && session.getAttribute("email") != null) {
            String email = (String) session.getAttribute("email");
            boolean isAdmin = session.getAttribute("admin") != null;
            String json = String.format("{\"email\":\"%s\",\"admin\":%s}", email, isAdmin);
            resp.getWriter().write(json);
        } else {
            resp.getWriter().write("{\"email\":null, \"admin\":false}");
        }
    }



}
