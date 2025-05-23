package br.com.cinetech.servlet;

import br.com.cinetech.dao.FilmeDAO;
import br.com.cinetech.model.FilmeModel;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/pesquisaFilme")
public class PesquisaFilme extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nome = request.getParameter("search");
        FilmeDAO dao = new FilmeDAO();
        List<FilmeModel> lista;

        if (nome != null && !nome.isEmpty()) {
            lista = dao.buscarPorNome(nome);
        } else {
            lista = dao.listarFilmes(null);
        }

        request.setAttribute("filmes", lista);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/v.jsp");
        dispatcher.forward(request, response);
    }
}


