package br.com.cinetech.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/menu-data")
public class menuServelt extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        if (session == null || session.getAttribute("admin") == null) {
            out.print("{\"admin\": false}");
            return;
        }

        String adm = (String) session.getAttribute("admin");
        boolean isAdmin = "adminn".equals(adm);

        out.print("{\"admin\": " + isAdmin + "}");
    }
}
