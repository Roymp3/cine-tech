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
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class FilmeServlet extends HttpServlet {    @Override
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
        boolean destaqueSemana = Boolean.parseBoolean(request.getParameter("destaqueSemana"));

        // Processar atores selecionados
        String atoresExistentesIdsStr = request.getParameter("atoresExistentesIds");
        String atoresNovosStr = request.getParameter("atoresNovos");
        
        // Criar o modelo do filme
        FilmeModel filme = new FilmeModel(nome, genero, sinopse, banner, bannerFixo, destaqueSemana);

        try {
            // Inserir o filme primeiro
            FilmeDAO filmeDao = new FilmeDAO();
            int filmeId = filmeDao.CreateFilme(filme);
            
            // Se o filme foi inserido com sucesso, processar os atores
            if (filmeId > 0) {
                processarAtoresDoFilme(filmeId, atoresExistentesIdsStr, atoresNovosStr);
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            response.sendError(500, "Erro ao cadastrar o filme: " + e.getMessage());
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
        
        // Endpoint para listar atores de um filme
        String action = request.getParameter("action");
        if (action != null && action.equals("listarAtores")) {
            String filmeIdStr = request.getParameter("filmeId");
            if (filmeIdStr != null) {
                try {
                    int filmeId = Integer.parseInt(filmeIdStr);
                    listarAtoresDoFilme(response, filmeId);
                    return;
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                    return;
                }
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
                jsonBuilder.append("{");
                jsonBuilder.append("\"id\":").append(filme.getId()).append(",");
                jsonBuilder.append("\"nome\":\"").append(escaparJSON(filme.getNome())).append("\",");
                jsonBuilder.append("\"genero\":\"").append(escaparJSON(filme.getGenero())).append("\",");                jsonBuilder.append("\"sinopse\":\"").append(escaparJSON(filme.getSinopse())).append("\",");
                jsonBuilder.append("\"destaqueSemana\":").append(filme.isDestaqueSemana()).append(",");
                // Agora vamos enviar uma URL para as imagens em vez de base64 direto
                jsonBuilder.append("\"bannerUrl\":\"").append("cadastrarFilme?imagem=banner&id=").append(filme.getId()).append("\",");
                jsonBuilder.append("\"bannerFixoUrl\":\"").append("cadastrarFilme?imagem=bannerFixo&id=").append(filme.getId()).append("\",");
                
                // Adicionar informações dos atores do filme
                java.util.List<java.util.Map<String, Object>> atoresDoFilme = filmeDAO.buscarAtoresDoFilme(filme.getId());
                jsonBuilder.append("\"atores\":[");
                for (int j = 0; j < atoresDoFilme.size(); j++) {
                    java.util.Map<String, Object> ator = atoresDoFilme.get(j);
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"id\":").append(ator.get("id")).append(",");
                    jsonBuilder.append("\"nome\":\"").append(escaparJSON((String)ator.get("nome"))).append("\"");
                    jsonBuilder.append("}");
                    if (j < atoresDoFilme.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("]");
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

    /**
     * Processa os atores do filme, adicionando novos atores e vinculando-os ao filme
     */
    private void processarAtoresDoFilme(int filmeId, String atoresExistentesIdsStr, String atoresNovosStr) {
        try {
            // Lista para armazenar todos os IDs de atores que serão vinculados ao filme
            java.util.List<Integer> atoresIds = new java.util.ArrayList<>();
            
            // Converter os IDs de atores existentes
            if (atoresExistentesIdsStr != null && !atoresExistentesIdsStr.isEmpty()) {
                // O formato esperado é um array JSON: [1,2,3]
                atoresExistentesIdsStr = atoresExistentesIdsStr.replace("[", "").replace("]", "").trim();
                if (!atoresExistentesIdsStr.isEmpty()) {
                    String[] idsArray = atoresExistentesIdsStr.split(",");
                    for (String idStr : idsArray) {
                        try {
                            int id = Integer.parseInt(idStr.trim());
                            atoresIds.add(id);
                        } catch (NumberFormatException e) {
                            System.out.println("ID de ator inválido: " + idStr);
                        }
                    }
                }
            }
            
            // Processar novos atores (se houver)
            if (atoresNovosStr != null && !atoresNovosStr.isEmpty()) {
                // O formato esperado é um array JSON: ["Nome1","Nome2"]
                atoresNovosStr = atoresNovosStr.replace("[", "").replace("]", "").trim();
                if (!atoresNovosStr.isEmpty()) {
                    // Dividir considerando que os nomes podem conter vírgulas dentro de aspas
                    java.util.List<String> novosAtores = new java.util.ArrayList<>();
                    
                    // Remover as aspas e separar por vírgula
                    boolean dentroDeAspas = false;
                    StringBuilder nomeAtual = new StringBuilder();
                    
                    for (int i = 0; i < atoresNovosStr.length(); i++) {
                        char c = atoresNovosStr.charAt(i);
                        
                        if (c == '"') {
                            dentroDeAspas = !dentroDeAspas;
                        } else if (c == ',' && !dentroDeAspas) {
                            // Fim de um nome
                            String nome = nomeAtual.toString().trim();
                            if (!nome.isEmpty()) {
                                novosAtores.add(nome);
                            }
                            nomeAtual = new StringBuilder();
                        } else if (c != ' ' || dentroDeAspas) {
                            // Adicionar ao nome atual (ignorar espaços fora de aspas)
                            nomeAtual.append(c);
                        }
                    }
                    
                    // Adicionar o último nome (se houver)
                    String ultimoNome = nomeAtual.toString().trim();
                    if (!ultimoNome.isEmpty()) {
                        novosAtores.add(ultimoNome);
                    }
                    
                    // Criar os novos atores e obter seus IDs
                    br.com.cinetech.dao.AtorDAO atorDAO = new br.com.cinetech.dao.AtorDAO();
                    for (String nome : novosAtores) {
                        // Remover aspas do início e fim
                        nome = nome.replaceAll("^\"|\"$", "");
                        if (!nome.isEmpty()) {
                            // Verificar se o ator já existe
                            br.com.cinetech.model.AtorModel atorExistente = null;
                            java.util.List<br.com.cinetech.model.AtorModel> atoresEncontrados = atorDAO.buscarPorNome(nome);
                            if (!atoresEncontrados.isEmpty()) {
                                atorExistente = atoresEncontrados.get(0);
                            }
                            
                            if (atorExistente != null) {
                                // Usar o ator existente
                                atoresIds.add(atorExistente.getIdAtor());
                            } else {
                                // Criar novo ator
                                br.com.cinetech.model.AtorModel novoAtor = new br.com.cinetech.model.AtorModel();
                                novoAtor.setNmAtor(nome);
                                if (atorDAO.inserir(novoAtor)) {
                                    // Obter o ID do ator recém-inserido
                                    atoresEncontrados = atorDAO.buscarPorNome(nome);
                                    if (!atoresEncontrados.isEmpty()) {
                                        atoresIds.add(atoresEncontrados.get(0).getIdAtor());
                                    }
                                }
                            }
                        }
                    }
                }
            }
              // Se temos atores para vincular, fazer a vinculação com o filme
            if (!atoresIds.isEmpty()) {
                System.out.println("Vinculando " + atoresIds.size() + " atores ao filme ID " + filmeId);
                FilmeDAO filmeDAO = new FilmeDAO();
                filmeDAO.vincularAtores(filmeId, atoresIds);
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao processar atores do filme: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para servir os atores de um filme em formato JSON
    private void listarAtoresDoFilme(HttpServletResponse response, int filmeId) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            FilmeDAO filmeDAO = new FilmeDAO();
            java.util.List<java.util.Map<String, Object>> atores = filmeDAO.buscarAtoresDoFilme(filmeId);
            
            // Montar JSON manualmente
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < atores.size(); i++) {
                java.util.Map<String, Object> ator = atores.get(i);
                jsonBuilder.append("{");
                jsonBuilder.append("\"id\":").append(ator.get("id")).append(",");
                jsonBuilder.append("\"nome\":\"").append(escaparJSON((String)ator.get("nome"))).append("\"");
                jsonBuilder.append("}");
                
                if (i < atores.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            out.print(jsonBuilder.toString());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"" + escaparJSON(e.getMessage()) + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
}
