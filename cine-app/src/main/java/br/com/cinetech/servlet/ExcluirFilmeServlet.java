package br.com.cinetech.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import br.com.cinetech.dao.FilmeDAO;

@WebServlet("/excluirFilme")
public class ExcluirFilmeServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verifica se o usuário está logado como administrador
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        
        if (email == null || !email.contains("@cinetech.com")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Acesso negado. Apenas administradores podem excluir filmes.\"}");
            return;
        }
        
        // Obtém o ID do filme a ser excluído
        String idFilmeStr = request.getParameter("idFilme");
        if (idFilmeStr == null || idFilmeStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID do filme não fornecido.\"}");
            return;
        }
        
        try {
            int idFilme = Integer.parseInt(idFilmeStr);
            
            FilmeDAO filmeDAO = new FilmeDAO();
            boolean sucesso = filmeDAO.DeleteFilme(idFilme);
            
            if (sucesso) {
                out.print("{\"success\": true, \"message\": \"Filme excluído com sucesso.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Erro ao excluir o filme.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID do filme inválido.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Erro ao processar a solicitação: " + e.getMessage() + "\"}");
        }
    }
}
