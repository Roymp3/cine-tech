package br.com.cinetech.servlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import br.com.cinetech.dao.FilmeDAO;
import br.com.cinetech.model.FilmeModel;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.io.PrintWriter;

@WebServlet("/cadastrarFilme")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // 5MB
public class FilmeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");


        request.setCharacterEncoding("UTF-8");
        String nome = request.getParameter("nome");
        String genero = request.getParameter("genero");
        String sinopse = request.getParameter("sinopse");

        Part filePart = request.getPart("banner");
        byte[] banner = extractBytesFromInputStream(filePart.getInputStream());
        
        Part filePartFixo = request.getPart("bannerFixo");
        byte[] bannerFixo = extractBytesFromInputStream(filePartFixo.getInputStream());

        FilmeModel filme = new FilmeModel(nome, genero, sinopse, banner, bannerFixo);

        try {
            FilmeDAO filmeDao = new FilmeDAO();
            filmeDao.CreateFilme(filme);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            response.sendError(500, "Erro ao cadastrar o filme.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Verificar se é uma requisição de imagem
        String imagemParam = request.getParameter("imagem");
        String idParam = request.getParameter("id");
        
        if (imagemParam != null && idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                servirImagemFilme(response, id, imagemParam);
                return;
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                return;
            }
        }
        
        // Comportamento normal para listagem de filmes
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obter parâmetro de filtro por gênero (opcional)
        request.setCharacterEncoding("UTF-8");
        String genero = request.getParameter("genero");
        
        try {
            FilmeDAO filmeDAO = new FilmeDAO();
            java.util.List<FilmeModel> filmes = filmeDAO.listarFilmes(genero);
            
            // Montar JSON manualmente
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < filmes.size(); i++) {
                FilmeModel filme = filmes.get(i);
                jsonBuilder.append("{");                jsonBuilder.append("\"id\":").append(filme.getId()).append(",");
                jsonBuilder.append("\"nome\":\"").append(escaparJSON(filme.getNome())).append("\",");
                jsonBuilder.append("\"genero\":\"").append(escaparJSON(filme.getGenero())).append("\",");
                jsonBuilder.append("\"sinopse\":\"").append(escaparJSON(filme.getSinopse())).append("\"");
                
                // Agora vamos enviar uma URL para as imagens em vez de base64 direto
                jsonBuilder.append(",\"bannerUrl\":").append("\"cadastrarFilme?imagem=banner&id=").append(filme.getId()).append("\"");
                jsonBuilder.append(",\"bannerFixoUrl\":").append("\"cadastrarFilme?imagem=bannerFixo&id=").append(filme.getId()).append("\"");
                
                jsonBuilder.append("}");
                
                if (i < filmes.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            
            // Enviar resposta JSON
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    private String escaparJSON(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

private byte[] extractBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void servirImagemFilme(HttpServletResponse response, int id, String tipoImagem) 
            throws ServletException, IOException {
        try {
            FilmeDAO filmeDAO = new FilmeDAO();
            FilmeModel filme = filmeDAO.GetFilme(id);
            
            if (filme == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Filme não encontrado");
                return;
            }
            
            byte[] imagem = null;
            
            if ("banner".equalsIgnoreCase(tipoImagem)) {
                imagem = filme.getBanner();
            } else if ("bannerFixo".equalsIgnoreCase(tipoImagem)) {
                imagem = filme.getBannerFixo();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de imagem inválido");
                return;
            }
            
            if (imagem == null || imagem.length == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagem não encontrada");
                return;
            }
            
            response.setContentType("image/jpeg");
            response.setContentLength(imagem.length);
            response.getOutputStream().write(imagem);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao buscar imagem");
        }
    }
}
