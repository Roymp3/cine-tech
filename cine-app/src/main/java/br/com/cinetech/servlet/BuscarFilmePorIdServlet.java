package br.com.cinetech.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.Gson;

import br.com.cinetech.dao.FilmeDAO;
import br.com.cinetech.dao.AtorDAO;
import br.com.cinetech.model.FilmeModel;
import br.com.cinetech.model.AtorModel;

@WebServlet("/buscarFilmePorId")
public class BuscarFilmePorIdServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verifica se o usuário está logado como administrador
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        
        if (email == null || !email.contains("@cinetech.com")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Acesso negado. Apenas administradores podem editar filmes.\"}");
            return;
        }
        
        // Obtém o ID do filme a ser buscado
        String idFilmeStr = request.getParameter("idFilme");
        if (idFilmeStr == null || idFilmeStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID do filme não fornecido.\"}");
            return;
        }
        
        try {
            int idFilme = Integer.parseInt(idFilmeStr);
            
            FilmeDAO filmeDAO = new FilmeDAO();
            FilmeModel filme = filmeDAO.GetFilmeById(idFilme);
            
            if (filme != null) {
                // Buscar atores relacionados ao filme
                AtorDAO atorDAO = new AtorDAO();
                List<AtorModel> atoresDoFilme = atorDAO.GetAtoresByFilmeId(idFilme);
                
                // Criar objeto de resposta
                FilmeEditarDTO resposta = new FilmeEditarDTO(filme, atoresDoFilme);
                
                Gson gson = new Gson();
                out.print(gson.toJson(resposta));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Filme não encontrado.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID do filme inválido.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Erro ao processar a solicitação: " + e.getMessage() + "\"}");
        }
    }
    
    // Classe interna para representar os dados do filme e seus atores
    private static class FilmeEditarDTO {
        private FilmeModel filme;
        private List<AtorModel> atores;
        
        public FilmeEditarDTO(FilmeModel filme, List<AtorModel> atores) {
            this.filme = filme;
            this.atores = atores;
        }
        
        public FilmeModel getFilme() {
            return filme;
        }
        
        public List<AtorModel> getAtores() {
            return atores;
        }
    }
}
