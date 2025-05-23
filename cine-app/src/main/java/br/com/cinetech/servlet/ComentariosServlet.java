package br.com.cinetech.servlet;

import br.com.cinetech.dao.ComentarioDAO;
import br.com.cinetech.dao.UserDao;
import br.com.cinetech.model.Comentario;
import br.com.cinetech.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet para gerenciar comentários de filmes
 */
@WebServlet("/comentarios")
public class ComentariosServlet extends HttpServlet {
    
    private final ComentarioDAO comentarioDAO = new ComentarioDAO();
    private final UserDao userDao = new UserDao();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configurar cabeçalhos de resposta
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Verificar o tipo de operação
        String action = request.getParameter("acao");
        if (action == null) {
            action = "listar"; // Ação padrão
        }
        
        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "listar":
                    listarComentarios(request, out);
                    break;
                
                case "verificarUsuario":
                    verificarUsuario(request, response, out);
                    break;
                
                default:
                    out.print("{\"error\": \"Ação não reconhecida\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(response.getWriter());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configurar cabeçalhos de resposta
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // Verificar o tipo de operação
        String action = request.getParameter("acao");
        if (action == null) {
            action = "adicionar"; // Ação padrão para POST
        }
        
        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "adicionar":
                    adicionarComentario(request, response, out);
                    break;
                    
                case "reagir":
                    reagirComentario(request, response, out);
                    break;
                    
                case "excluir":
                    excluirComentario(request, response, out);
                    break;
                    
                default:
                    out.print("{\"error\": \"Ação não reconhecida\"}");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(response.getWriter());
        }
    }
    
    /**
     * Lista todos os comentários de um filme
     */
    private void listarComentarios(HttpServletRequest request, PrintWriter out) {
        String idFilmeStr = request.getParameter("idFilme");
        
        if (idFilmeStr == null || idFilmeStr.trim().isEmpty()) {
            out.print("{\"error\": \"ID do filme não fornecido\"}");
            return;
        }
        
        try {
            int idFilme = Integer.parseInt(idFilmeStr);
            List<Comentario> comentarios = comentarioDAO.listarComentariosPorFilme(idFilme);
            
            // Construir JSON array com os comentários
            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < comentarios.size(); i++) {
                Comentario c = comentarios.get(i);
                json.append("{");
                json.append("\"id\":").append(c.getId()).append(",");
                json.append("\"idFilme\":").append(c.getIdFilme()).append(",");
                json.append("\"emailUsuario\":\"").append(c.getEmailUsuario()).append("\",");
                json.append("\"nomeUsuario\":\"").append(c.getNomeUsuario()).append("\",");
                json.append("\"texto\":\"").append(escapeJson(c.getTexto())).append("\",");
                json.append("\"dataComentario\":\"").append(dateFormat.format(c.getDataComentario())).append("\",");
                json.append("\"likes\":").append(c.getLikes()).append(",");
                json.append("\"dislikes\":").append(c.getDislikes()).append(",");
                json.append("\"avaliacao\":").append(c.getAvaliacao());
                json.append("}");
                
                if (i < comentarios.size() - 1) {
                    json.append(",");
                }
            }
            
            json.append("]");
            out.print(json.toString());
            
        } catch (NumberFormatException e) {
            out.print("{\"error\": \"ID do filme inválido\"}");
        }
    }
    
    /**
     * Adiciona um novo comentário
     */
    private void adicionarComentario(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        System.out.println("Iniciando processo de adicionar comentário...");
        
        // Verificar se o usuário está logado
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Erro: Sessão é null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Sessão não encontrada. Usuário não está logado.\"}");
            return;
        }
        
        System.out.println("Verificando atributos de sessão: logadoo=" + session.getAttribute("logadoo"));
        if (session.getAttribute("logadoo") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Usuário não está logado (atributo logadoo não encontrado)\"}");
            return;
        }
        
        String email = (String) session.getAttribute("email");
        System.out.println("Email na sessão: " + email);
        if (email == null || email.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Email do usuário não encontrado na sessão\"}");
            return;
        }
        
        // Tentar ler os dados do JSON no corpo da requisição
        String idFilmeStr = null;
        String texto = null;
        String avaliacaoStr = null;
        
        // Verificar se é uma requisição JSON
        System.out.println("Content-Type da requisição: " + request.getContentType());
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                // Ler o corpo da requisição como JSON
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                }
                
                // Parse do JSON
                String json = jsonBuilder.toString();
                System.out.println("JSON recebido para adicionar comentário: " + json);
                
                if (json != null && !json.isEmpty()) {
                    // Extração simples de valores do JSON
                    // Isso não é um parser JSON completo, mas funciona para estruturas simples
                    // idFilme
                    int idFilmeIndex = json.indexOf("\"idFilme\"");
                    if (idFilmeIndex >= 0) {
                        int colonIndex = json.indexOf(":", idFilmeIndex);
                        if (colonIndex >= 0) {
                            int commaIndex = json.indexOf(",", colonIndex);
                            if (commaIndex < 0) commaIndex = json.indexOf("}", colonIndex);
                            if (commaIndex >= 0) {
                                idFilmeStr = json.substring(colonIndex + 1, commaIndex).trim();
                                idFilmeStr = idFilmeStr.replace("\"", ""); // Remove aspas duplas se houver
                                System.out.println("idFilme encontrado: " + idFilmeStr);
                            }
                        }
                    }
                    
                    // texto
                    int textoIndex = json.indexOf("\"texto\"");
                    if (textoIndex >= 0) {
                        int colonIndex = json.indexOf(":", textoIndex);
                        if (colonIndex >= 0) {
                            int startQuote = json.indexOf("\"", colonIndex + 1);
                            if (startQuote >= 0) {
                                int endQuote = json.indexOf("\"", startQuote + 1);
                                while (endQuote > 0 && json.charAt(endQuote - 1) == '\\') {
                                    endQuote = json.indexOf("\"", endQuote + 1);
                                }
                                if (endQuote >= 0) {
                                    texto = json.substring(startQuote + 1, endQuote)
                                            .replace("\\\"", "\"")
                                            .replace("\\\\", "\\");
                                    System.out.println("texto encontrado: " + texto);
                                }
                            }
                        }
                    }
                    
                    // avaliacao
                    int avaliacaoIndex = json.indexOf("\"avaliacao\"");
                    if (avaliacaoIndex >= 0) {
                        int colonIndex = json.indexOf(":", avaliacaoIndex);
                        if (colonIndex >= 0) {
                            int commaIndex = json.indexOf(",", colonIndex);
                            if (commaIndex < 0) commaIndex = json.indexOf("}", colonIndex);
                            if (commaIndex >= 0) {
                                avaliacaoStr = json.substring(colonIndex + 1, commaIndex).trim();
                                System.out.println("avaliacao encontrada: " + avaliacaoStr);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar JSON: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Se não for JSON, tentar parâmetros normais de formulário
            idFilmeStr = request.getParameter("idFilme");
            texto = request.getParameter("texto");
            avaliacaoStr = request.getParameter("avaliacao");
            System.out.println("Requisição não-JSON: idFilme=" + idFilmeStr + ", texto=" + texto);
        }
        
        System.out.println("Parâmetros após processamento: idFilme=" + idFilmeStr + ", texto=" + (texto != null ? "presente" : "ausente"));
        if (idFilmeStr == null || texto == null || texto.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Parâmetros incompletos: idFilme=" + idFilmeStr + ", texto=" + (texto != null) + "\"}");
            return;
        }
        
        try {
            int idFilme = Integer.parseInt(idFilmeStr);
            int avaliacao = avaliacaoStr != null ? Integer.parseInt(avaliacaoStr) : 0;
            
            // Verificar se o usuário já comentou este filme
            System.out.println("Verificando se usuário já comentou: idFilme=" + idFilme + ", email=" + email);
            if (comentarioDAO.usuarioJaComentou(idFilme, email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"error\": \"Você já comentou neste filme\"}");
                return;
            }
            
            // Obter informações do usuário para o nome
            User user = userDao.buscarPorEmail(email);
            String nomeUsuario = user != null ? user.getNm_usuario() : email;
            System.out.println("Nome de usuário recuperado: " + nomeUsuario);
            
            // Criar e salvar o comentário
            Comentario comentario = new Comentario();
            comentario.setIdFilme(idFilme);
            comentario.setEmailUsuario(email);
            comentario.setNomeUsuario(nomeUsuario);
            comentario.setTexto(texto);
            comentario.setAvaliacao(avaliacao);
            
            System.out.println("Tentando adicionar comentário no banco de dados...");
            int idComentario = comentarioDAO.adicionarComentario(comentario);
            
            if (idComentario > 0) {
                comentario.setId(idComentario);
                System.out.println("Comentário adicionado com sucesso, ID: " + idComentario);
                
                // Retornar o comentário adicionado
                StringBuilder json = new StringBuilder("{");
                json.append("\"id\":").append(comentario.getId()).append(",");
                json.append("\"idFilme\":").append(comentario.getIdFilme()).append(",");
                json.append("\"emailUsuario\":\"").append(comentario.getEmailUsuario()).append("\",");
                json.append("\"nomeUsuario\":\"").append(comentario.getNomeUsuario()).append("\",");
                json.append("\"texto\":\"").append(escapeJson(comentario.getTexto())).append("\",");
                json.append("\"dataComentario\":\"").append(dateFormat.format(comentario.getDataComentario())).append("\",");
                json.append("\"likes\":").append(comentario.getLikes()).append(",");
                json.append("\"dislikes\":").append(comentario.getDislikes()).append(",");
                json.append("\"avaliacao\":").append(comentario.getAvaliacao());
                json.append("}");
                
                out.print(json.toString());
            } else {
                System.err.println("Erro: ID do comentário retornou <= 0");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Erro ao adicionar comentário\"}");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato de número: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Parâmetros inválidos: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Erro não esperado: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Erro interno: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Adiciona uma reação (like/dislike) a um comentário
     */
    private void reagirComentario(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idComentarioStr = null;
        String likeStr = null;
        
        // Verificar se é uma requisição JSON
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                // Ler o corpo da requisição como JSON
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                }
                
                // Parse do JSON
                String json = jsonBuilder.toString();
                if (json != null && !json.isEmpty()) {
                    // Usar regex para extrair os valores do JSON
                    Pattern idComentarioPattern = Pattern.compile("\"idComentario\"\\s*:\\s*([0-9]+)");
                    Pattern likePattern = Pattern.compile("\"like\"\\s*:\\s*(true|false)");
                    
                    Matcher idComentarioMatcher = idComentarioPattern.matcher(json);
                    if (idComentarioMatcher.find()) {
                        idComentarioStr = idComentarioMatcher.group(1);
                    }
                    
                    Matcher likeMatcher = likePattern.matcher(json);
                    if (likeMatcher.find()) {
                        likeStr = likeMatcher.group(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar JSON: " + e.getMessage());
            }
        } else {
            // Se não for JSON, tentar parâmetros normais de formulário
            idComentarioStr = request.getParameter("idComentario");
            likeStr = request.getParameter("like");
        }
        
        if (idComentarioStr == null || likeStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Parâmetros incompletos\"}");
            return;
        }
        
        try {
            int idComentario = Integer.parseInt(idComentarioStr);
            boolean isLike = Boolean.parseBoolean(likeStr);
            
            boolean sucesso = comentarioDAO.atualizarReacao(idComentario, isLike ? 1 : -1);
            
            if (sucesso) {
                out.print("{\"success\": true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Comentário não encontrado\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID do comentário inválido\"}");
        }
    }
    
    /**
     * Exclui um comentário
     */
    private void excluirComentario(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        String idComentarioStr = null;
        
        // Verificar se é uma requisição JSON
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                // Ler o corpo da requisição como JSON
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                }
                
                // Parse do JSON
                String json = jsonBuilder.toString();
                if (json != null && !json.isEmpty()) {
                    // Usar regex para extrair os valores do JSON
                    Pattern idComentarioPattern = Pattern.compile("\"idComentario\"\\s*:\\s*([0-9]+)");
                    
                    Matcher idComentarioMatcher = idComentarioPattern.matcher(json);
                    if (idComentarioMatcher.find()) {
                        idComentarioStr = idComentarioMatcher.group(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar JSON: " + e.getMessage());
            }
        } else {
            // Se não for JSON, tentar parâmetros normais de formulário
            idComentarioStr = request.getParameter("idComentario");
        }
        
        if (idComentarioStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID do comentário não fornecido\"}");
            return;
        }
        
        // Verificar se o usuário está logado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("logadoo") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"Usuário não está logado\"}");
            return;
        }
        
        String email = (String) session.getAttribute("email");
        boolean isAdmin = session.getAttribute("admin") != null;
        
        try {
            int idComentario = Integer.parseInt(idComentarioStr);
            boolean sucesso;
            
            // Administradores podem excluir qualquer comentário, usuários apenas os seus
            if (isAdmin) {
                sucesso = comentarioDAO.excluirComentarioAdmin(idComentario);
            } else {
                sucesso = comentarioDAO.excluirComentario(idComentario, email);
            }
            
            if (sucesso) {
                out.print("{\"success\": true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Comentário não encontrado ou permissão negada\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID do comentário inválido\"}");
        }
    }
    
    /**
     * Verifica se o usuário está logado e pode comentar
     */
    private void verificarUsuario(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        System.out.println("Verificando status do usuário...");
        
        HttpSession session = request.getSession(false);
        System.out.println("Sessão: " + (session != null ? "existe" : "null"));
        
        boolean logado = false;
        if (session != null) {
            System.out.println("Atributos de sessão - logadoo: " + session.getAttribute("logadoo") + 
                             ", email: " + session.getAttribute("email") +
                             ", admin: " + session.getAttribute("admin"));
            logado = session.getAttribute("logadoo") != null;
        }
        
        StringBuilder json = new StringBuilder("{");
        json.append("\"logado\":").append(logado);
        
        if (logado) {
            String email = (String) session.getAttribute("email");
            boolean isAdmin = session.getAttribute("admin") != null;
            
            json.append(",\"email\":\"").append(email).append("\"");
            json.append(",\"admin\":").append(isAdmin);
            
            // Verificar se já comentou no filme
            String idFilmeStr = request.getParameter("idFilme");
            if (idFilmeStr != null && !idFilmeStr.trim().isEmpty()) {
                try {
                    int idFilme = Integer.parseInt(idFilmeStr);
                    boolean jaComentou = comentarioDAO.usuarioJaComentou(idFilme, email);
                    json.append(",\"jaComentou\":").append(jaComentou);
                    System.out.println("Usuário já comentou: " + jaComentou);
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter idFilme: " + e.getMessage());
                }
            }
            
            // Buscar nome do usuário
            User user = userDao.buscarPorEmail(email);
            System.out.println("Usuário buscado por email: " + (user != null ? "encontrado" : "não encontrado"));
            if (user != null && user.getNm_usuario() != null) {
                json.append(",\"nome\":\"").append(user.getNm_usuario()).append("\"");
                System.out.println("Nome do usuário: " + user.getNm_usuario());
            } else {
                json.append(",\"nome\":\"").append(email).append("\"");
                System.out.println("Nome do usuário não encontrado, usando email");
            }
        } else {
            System.out.println("Usuário não está logado");
        }
        
        json.append("}");
        String jsonString = json.toString();
        System.out.println("JSON de resposta: " + jsonString);
        out.print(jsonString);
    }
    
    /**
     * Escape caracteres especiais para JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Configurar cabeçalhos CORS para preflight
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
