package br.com.cinetech.servlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import br.com.cinetech.dao.AtorDAO;
import br.com.cinetech.model.AtorModel;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/cadastrarAtor", "/listarAtores", "/buscarAtor", "/obterFotoAtor", "/buscarAtores", "/buscarAtorPorNome", "/atoresPorFilme"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class AtorServlet extends HttpServlet {    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();
        
        switch (action) {
            case "/cadastrarAtor":
                cadastrarAtor(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }      @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        String idParam = request.getParameter("id");
        
        if (idParam != null && request.getServletPath().equals("/obterFotoAtor")) {
            try {
                int id = Integer.parseInt(idParam);
                servirFotoAtor(response, id);
                return;
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
                return;
            }
        }
          String action = request.getServletPath();
        String atoresFilme = request.getParameter("idFilme");
        
        // Handle accessing buscarAtores with idFilme parameter for backward compatibility
        if (atoresFilme != null && !atoresFilme.trim().isEmpty() && !action.equals("/atoresPorFilme")) {
            buscarAtoresPorFilme(request, response);
            return;
        }
          switch (action) {
            case "/listarAtores":
                listarAtores(request, response);
                break;
            case "/buscarAtor":
                buscarAtor(request, response);
                break;
            case "/buscarAtores":
                buscarAtoresPorTermo(request, response);
                break;
            case "/buscarAtorPorNome":
                buscarAtorPorNomeExato(request, response);
                break;
            case "/atoresPorFilme":
                buscarAtoresPorFilme(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }
  
    private void cadastrarAtor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("Iniciando processamento de cadastro de ator");
        
        try {

            String nome = request.getParameter("nome");
            String biografia = request.getParameter("biografia");
            String nacionalidade = request.getParameter("nacionalidade");
            String premios = request.getParameter("premios");
            String filmesFamosos = request.getParameter("filmesFamosos");
              if (nome == null || nome.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Nome do ator é obrigatório\"}");
                return;
            }
              System.out.println("Nome recebido: " + nome);
            System.out.println("Biografia recebida: " + biografia);
            System.out.println("Nacionalidade recebida: " + nacionalidade);
            
            Date dataNascimento = null;
            String dataNascimentoStr = request.getParameter("dataNascimento");
            if (dataNascimentoStr != null && !dataNascimentoStr.isEmpty()) {
                System.out.println("Data recebida: " + dataNascimentoStr);
                try {
                    dataNascimento = Date.valueOf(dataNascimentoStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro ao converter data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
              Part fotoPart = null;
        try {
            fotoPart = request.getPart("foto");
            System.out.println("Foto recebida: " + (fotoPart != null ? fotoPart.getSize() + " bytes" : "null"));
        } catch (Exception e) {
            System.out.println("Erro ao obter parte 'foto': " + e.getMessage());
            e.printStackTrace();
        }
        
        byte[] fotoBytes = null;
        if (fotoPart != null && fotoPart.getSize() > 0) {
            try (InputStream inputStream = fotoPart.getInputStream()) {
                fotoBytes = extractBytesFromInputStream(inputStream);
                System.out.println("Foto processada com sucesso, tamanho: " + (fotoBytes != null ? fotoBytes.length : 0) + " bytes");
            } catch (IOException e) {
                System.out.println("Erro ao processar imagem: " + e.getMessage());
                e.printStackTrace();
            }
        }
          AtorModel ator = new AtorModel();
        ator.setNmAtor(nome);
        ator.setDsBiografia(biografia);
        ator.setDtNascimento(dataNascimento);
        ator.setNmNacionalidade(nacionalidade);
        ator.setDsPremios(premios);
        ator.setDsFilmesFamosos(filmesFamosos);
        ator.setFotoBytes(fotoBytes);
        
        try {
            System.out.println("Criando objeto AtorDAO");
            AtorDAO atorDAO = new AtorDAO();
            
            System.out.println("Iniciando inserção no banco de dados");
            boolean resultado = atorDAO.inserir(ator);
            System.out.println("Resultado da inserção: " + resultado);
            
            System.out.println("Configurando resposta JSON");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            
            if (resultado) {
                System.out.println("Enviando resposta de sucesso");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\",\"message\":\"Ator cadastrado com sucesso\"}");
            } else {
                System.out.println("Enviando resposta de erro");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"status\":\"error\",\"message\":\"Erro ao cadastrar o ator\"}");
            }
            out.flush();
            System.out.println("Resposta enviada com sucesso");
        } catch (Exception e) {
            System.out.println("Exceção capturada durante o processamento: " + e.getClass().getName());
            e.printStackTrace();
            
            System.out.println("Configurando resposta de erro");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            String errorMessage = e.getMessage() != null ? escaparJSON(e.getMessage()) : "Erro desconhecido";
            System.out.println("Mensagem de erro: " + errorMessage);
            out.print("{\"status\":\"error\",\"message\":\"" + errorMessage + "\"}");
            out.flush();
            System.out.println("Resposta de erro enviada");
        }
    } catch (Exception e) {
        System.out.println("Exceção capturada ao processar formulário: " + e.getClass().getName());
        e.printStackTrace();
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        PrintWriter out = response.getWriter();
        String errorMessage = e.getMessage() != null ? escaparJSON(e.getMessage()) : "Erro ao processar o formulário";
        out.print("{\"status\":\"error\",\"message\":\"" + errorMessage + "\"}");
        out.flush();
    }
    }
  
    private void listarAtores(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            System.out.println("Executando listagem de todos os atores");
            AtorDAO atorDAO = new AtorDAO();
            List<AtorModel> atores = atorDAO.listarTodos();
            System.out.println("Total de atores encontrados: " + atores.size());
            
            // Montar JSON manualmente
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < atores.size(); i++) {
                AtorModel ator = atores.get(i);
                jsonBuilder.append("{");
                jsonBuilder.append("\"id\":").append(ator.getIdAtor()).append(",");
                jsonBuilder.append("\"nome\":\"").append(escaparJSON(ator.getNmAtor())).append("\",");
                jsonBuilder.append("\"biografia\":\"").append(escaparJSON(ator.getDsBiografia())).append("\",");
                jsonBuilder.append("\"dataNascimento\":\"").append(ator.getDtNascimento() != null ? ator.getDtNascimento() : "").append("\",");
                jsonBuilder.append("\"nacionalidade\":\"").append(escaparJSON(ator.getNmNacionalidade())).append("\",");
                jsonBuilder.append("\"premios\":\"").append(escaparJSON(ator.getDsPremios())).append("\",");
                jsonBuilder.append("\"filmesFamosos\":\"").append(escaparJSON(ator.getDsFilmesFamosos())).append("\",");
                jsonBuilder.append("\"fotoUrl\":\"").append("obterFotoAtor?id=").append(ator.getIdAtor()).append("\"");
                jsonBuilder.append("}");
                
                if (i < atores.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            
            // Enviar resposta JSON
            System.out.println("Enviando resposta JSON com lista de atores");
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.toString());
            out.flush();
            
        } catch (Exception e) {
            System.out.println("Erro ao listar atores: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + escaparJSON(e.getMessage()) + "\"}");
        }
    }
  
    private void buscarAtor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idParam = request.getParameter("id");
            String nomeParam = request.getParameter("nome");
            AtorDAO atorDAO = new AtorDAO();
            
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                AtorModel ator = atorDAO.buscarPorId(id);
                
                if (ator != null) {
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"id\":").append(ator.getIdAtor()).append(",");
                    jsonBuilder.append("\"nome\":\"").append(escaparJSON(ator.getNmAtor())).append("\",");
                    jsonBuilder.append("\"biografia\":\"").append(escaparJSON(ator.getDsBiografia())).append("\",");
                    jsonBuilder.append("\"dataNascimento\":\"").append(ator.getDtNascimento() != null ? ator.getDtNascimento() : "").append("\",");
                    jsonBuilder.append("\"nacionalidade\":\"").append(escaparJSON(ator.getNmNacionalidade())).append("\",");
                    jsonBuilder.append("\"premios\":\"").append(escaparJSON(ator.getDsPremios())).append("\",");
                    jsonBuilder.append("\"filmesFamosos\":\"").append(escaparJSON(ator.getDsFilmesFamosos())).append("\",");
                    jsonBuilder.append("\"fotoUrl\":\"").append("obterFotoAtor?id=").append(ator.getIdAtor()).append("\"");
                    jsonBuilder.append("}");
                    
                    PrintWriter out = response.getWriter();
                    out.print(jsonBuilder.toString());
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Ator não encontrado\"}");
                }
            } else if (nomeParam != null && !nomeParam.isEmpty()) {
                List<AtorModel> atores = atorDAO.buscarPorNome(nomeParam);
                
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("[");
                
                for (int i = 0; i < atores.size(); i++) {
                    AtorModel ator = atores.get(i);
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"id\":").append(ator.getIdAtor()).append(",");
                    jsonBuilder.append("\"nome\":\"").append(escaparJSON(ator.getNmAtor())).append("\",");
                    jsonBuilder.append("\"biografia\":\"").append(escaparJSON(ator.getDsBiografia())).append("\",");
                    jsonBuilder.append("\"dataNascimento\":\"").append(ator.getDtNascimento() != null ? ator.getDtNascimento() : "").append("\",");
                    jsonBuilder.append("\"nacionalidade\":\"").append(escaparJSON(ator.getNmNacionalidade())).append("\",");
                    jsonBuilder.append("\"premios\":\"").append(escaparJSON(ator.getDsPremios())).append("\",");
                    jsonBuilder.append("\"filmesFamosos\":\"").append(escaparJSON(ator.getDsFilmesFamosos())).append("\",");
                    jsonBuilder.append("\"fotoUrl\":\"").append("obterFotoAtor?id=").append(ator.getIdAtor()).append("\"");
                    jsonBuilder.append("}");
                    
                    if (i < atores.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
                
                jsonBuilder.append("]");
                
                PrintWriter out = response.getWriter();
                out.print(jsonBuilder.toString());
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"É necessário fornecer um ID ou nome para busca\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
  
    /**
     * Busca atores baseado em um termo de pesquisa parcial para o autocomplete
     */
    private void buscarAtoresPorTermo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String termo = request.getParameter("termo");
        if (termo == null || termo.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"O termo de busca é obrigatório\"}");
            return;
        }
        
        try {
            AtorDAO atorDAO = new AtorDAO();
            List<AtorModel> atores = atorDAO.buscarPorNome(termo);
            
            // Converter para formato simplificado para o autocomplete
            StringBuilder jsonBuilder = new StringBuilder("[");
            boolean primeiro = true;
            
            for (AtorModel ator : atores) {
                if (!primeiro) {
                    jsonBuilder.append(",");
                }
                primeiro = false;
                
                jsonBuilder.append("{")
                        .append("\"id\":").append(ator.getIdAtor()).append(",")
                        .append("\"nome\":\"").append(ator.getNmAtor().replace("\"", "\\\"")).append("\"")
                        .append("}");
            }
            jsonBuilder.append("]");
            
            out.print(jsonBuilder.toString());
            atorDAO.fecharConexao();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Erro ao buscar atores: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Busca um ator pelo nome exato
     */
    private void buscarAtorPorNomeExato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String nome = request.getParameter("nome");
        if (nome == null || nome.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"O nome do ator é obrigatório\"}");
            return;
        }
          try {
            AtorDAO atorDAO = new AtorDAO();
            // Usando o novo método para busca case-insensitive exata
            List<AtorModel> atores = atorDAO.buscarPorNomeExato(nome);
            
            if (!atores.isEmpty()) {
                // Se encontrar correspondências exatas, retorna a primeira
                AtorModel ator = atores.get(0);
                StringBuilder jsonBuilder = new StringBuilder();
                
                jsonBuilder.append("{")
                        .append("\"id\":").append(ator.getIdAtor()).append(",")
                        .append("\"nome\":\"").append(ator.getNmAtor().replace("\"", "\\\"")).append("\"")
                        .append("}");
                
                out.print(jsonBuilder.toString());
            } else {
                // Se não encontrar, retorna objeto vazio
                out.print("{}");
            }
            
            atorDAO.fecharConexao();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Erro ao buscar ator: " + e.getMessage() + "\"}");
        }
    }
  
    private void servirFotoAtor(HttpServletResponse response, int id) throws ServletException, IOException {
        System.out.println("Servindo foto do ator com ID: " + id);
        
        try {
            AtorDAO atorDAO = new AtorDAO();
            AtorModel ator = atorDAO.buscarPorId(id);
            
            if (ator == null) {
                System.out.println("Ator não encontrado com ID: " + id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ator não encontrado");
                return;
            }
            

            byte[] fotoBytes = ator.getFotoBytes();
            if (fotoBytes == null && ator.getImgFoto() != null) {
                System.out.println("Convertendo InputStream para bytes");
                fotoBytes = extractBytesFromInputStream(ator.getImgFoto());
            }
            
            if (fotoBytes != null && fotoBytes.length > 0) {
                System.out.println("Enviando imagem com tamanho: " + fotoBytes.length + " bytes");
                response.setContentType("image/jpeg");
                response.setContentLength(fotoBytes.length);
                response.getOutputStream().write(fotoBytes);
                response.getOutputStream().flush();
                System.out.println("Imagem enviada com sucesso");
            } else {
                System.out.println("Imagem não encontrada ou vazia para o ator ID: " + id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagem não encontrada");
            }
            
        } catch (Exception e) {
            System.out.println("Erro ao servir a foto do ator: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao buscar imagem: " + e.getMessage());
        }
    }
    

    private byte[] extractBytesFromInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            System.out.println("InputStream é nulo, retornando array vazio");
            return new byte[0];
        }
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            
            System.out.println("Lendo dados do InputStream");
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, read);
            }
            
            baos.flush();
            byte[] result = baos.toByteArray();
            System.out.println("Bytes extraídos com sucesso. Tamanho: " + result.length + " bytes");
            return result;
        } catch (IOException e) {
            System.out.println("Erro ao ler InputStream: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
  
    private void buscarAtoresPorFilme(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String idFilmeParam = request.getParameter("idFilme");
        
        if (idFilmeParam == null || idFilmeParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"ID do filme não informado\"}");
            return;
        }
        
        try {
            int idFilme = Integer.parseInt(idFilmeParam);
            AtorDAO atorDao = new AtorDAO();
            List<AtorModel> atores = atorDao.buscarAtoresPorFilme(idFilme);
            
            // Construir JSON com a lista de atores
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            for (int i = 0; i < atores.size(); i++) {
                AtorModel ator = atores.get(i);                jsonBuilder.append("{");
                jsonBuilder.append("\"id\":").append(ator.getIdAtor()).append(",");
                jsonBuilder.append("\"nome\":\"").append(escaparJSON(ator.getNmAtor())).append("\"");
                jsonBuilder.append("}");
                
                if (i < atores.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            
            jsonBuilder.append("]");
            out.print(jsonBuilder.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"erro\":\"ID do filme inválido\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"erro\":\"" + escaparJSON(e.getMessage()) + "\"}");
            e.printStackTrace();
        }
    }
    
    /**
     * Escapa caracteres especiais para JSON
     * @param input String a ser escapada
     * @return String escapada
     */
    private String escaparJSON(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
