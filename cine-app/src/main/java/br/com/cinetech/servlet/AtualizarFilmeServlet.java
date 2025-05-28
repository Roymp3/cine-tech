package br.com.cinetech.servlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

import br.com.cinetech.dao.FilmeDAO;
import br.com.cinetech.model.FilmeModel;

@WebServlet("/atualizarFilme")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class AtualizarFilmeServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar se o usuário é um administrador
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        
        if (email == null || !email.contains("@cinetech.com")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\": false, \"message\": \"Acesso negado. Apenas administradores podem atualizar filmes.\"}");
            return;
        }
        
        try {
            // Extrair parâmetros do formulário
            String idFilmeStr = request.getParameter("idFilme");
            if (idFilmeStr == null || idFilmeStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"ID do filme não fornecido.\"}");
                return;
            }
            
            int idFilme = Integer.parseInt(idFilmeStr);
            String nome = request.getParameter("nome");
            String genero = request.getParameter("genero");
            String sinopse = request.getParameter("sinopse");
            boolean destaqueSemana = Boolean.parseBoolean(request.getParameter("destaqueSemana"));
            
            // Buscar o filme atual para manter os banners caso não sejam fornecidos novos
            FilmeDAO filmeDAO = new FilmeDAO();
            FilmeModel filmeAtual = filmeDAO.GetFilmeById(idFilme);
            
            if (filmeAtual == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Filme não encontrado.\"}");
                return;
            }
            
            // Extrair imagens das partes, se fornecidas
            byte[] banner = filmeAtual.getBanner();
            Part bannerPart = request.getPart("banner");
            if (bannerPart != null && bannerPart.getSize() > 0) {
                banner = extractBytesFromInputStream(bannerPart.getInputStream());
            }
            
            byte[] bannerFixo = filmeAtual.getBannerFixo();
            Part bannerFixoPart = request.getPart("bannerFixo");
            if (bannerFixoPart != null && bannerFixoPart.getSize() > 0) {
                bannerFixo = extractBytesFromInputStream(bannerFixoPart.getInputStream());
            }
            
            // Criar o modelo atualizado do filme
            FilmeModel filmeAtualizado = new FilmeModel(
                idFilme, 
                nome, 
                genero, 
                sinopse, 
                banner, 
                bannerFixo, 
                destaqueSemana
            );
            
            // Atualizar o filme no banco de dados
            boolean sucesso = filmeDAO.UpdateFilme(filmeAtualizado);
            
            if (sucesso) {
                out.print("{\"success\": true, \"message\": \"Filme atualizado com sucesso.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Erro ao atualizar o filme.\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID do filme inválido.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Erro ao processar a solicitação: " + e.getMessage() + "\"}");
        }
    }
    
    // Método auxiliar para extrair bytes de um InputStream
    private byte[] extractBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        return buffer.toByteArray();
    }
}
