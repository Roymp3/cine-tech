package br.com.cinetech.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.cinetech.dao.FilmeAcessoDAO;

/**
 * Servlet para fornecer a lista de filmes mais acessados para o dashboard
 */
@WebServlet("/filmesMaisAcessados")
public class FilmesMaisAcessadosServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("FilmesMaisAcessadosServlet: Processando requisição GET");
        
        // Configurar cabeçalhos CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        
        // Se for uma requisição OPTIONS (preflight), apenas retornar os cabeçalhos
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        // Configurar tipo de conteúdo para JSON
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String ordem = request.getParameter("ordem");
        boolean ordenarMaior = "maior".equalsIgnoreCase(ordem);
        System.out.println("FilmesMaisAcessadosServlet: Ordem solicitada: " + ordem);
        
        try {
            // Usar o DAO para buscar filmes mais acessados (limite de 7 filmes)
            FilmeAcessoDAO acessoDAO = new FilmeAcessoDAO();
            List<Map<String, Object>> filmes = acessoDAO.listarFilmesMaisAcessados(ordem, 7);
            System.out.println("FilmesMaisAcessadosServlet: Filmes encontrados: " + filmes.size());
            
            // Se não houver registros no banco de dados, usar dados de exemplo
            if (filmes.isEmpty()) {
                System.out.println("FilmesMaisAcessadosServlet: Nenhum filme encontrado, usando dados de exemplo");
                // Dados de exemplo - no futuro, isso deve vir do banco de dados
                String[][] filmesExemplo = {
                    {"Interestelar", "325"},
                    {"Homem-Aranha", "287"},
                    {"Avatar", "245"},
                    {"Matrix", "198"},
                    {"O Senhor dos Anéis", "156"},
                    {"Vingadores", "143"},
                    {"Star Wars", "132"}
                };
                
                // Ordenar conforme solicitado (ordenação simples para este exemplo)
                if (!ordenarMaior) {
                    // Ordenar do menor para o maior
                    for (int i = 0; i < filmesExemplo.length; i++) {
                        for (int j = i + 1; j < filmesExemplo.length; j++) {
                            int acessosI = Integer.parseInt(filmesExemplo[i][1]);
                            int acessosJ = Integer.parseInt(filmesExemplo[j][1]);
                            if (acessosI > acessosJ) {
                                String[] temp = filmesExemplo[i];
                                filmesExemplo[i] = filmesExemplo[j];
                                filmesExemplo[j] = temp;
                            }
                        }
                    }
                }
                
                // Construir array JSON com os filmes mais acessados
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("[");
                
                for (int i = 0; i < filmesExemplo.length; i++) {
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"nome\":\"").append(filmesExemplo[i][0]).append("\",");
                    jsonBuilder.append("\"acessos\":").append(filmesExemplo[i][1]);
                    jsonBuilder.append("}");
                    
                    if (i < filmesExemplo.length - 1) {
                        jsonBuilder.append(",");
                    }
                }
                
                jsonBuilder.append("]");
                
                String jsonResponse = jsonBuilder.toString();
                System.out.println("FilmesMaisAcessadosServlet: Resposta JSON (dados de exemplo): " + jsonResponse);
                
                PrintWriter out = response.getWriter();
                out.print(jsonResponse);
                out.flush();
            } else {
                // Construir array JSON com os dados reais do banco
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("[");
                
                for (int i = 0; i < filmes.size(); i++) {
                    Map<String, Object> filme = filmes.get(i);
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"nome\":\"").append(filme.get("nome")).append("\",");
                    jsonBuilder.append("\"acessos\":").append(filme.get("acessos"));
                    jsonBuilder.append("}");
                    
                    if (i < filmes.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
                
                jsonBuilder.append("]");
                
                String jsonResponse = jsonBuilder.toString();
                System.out.println("FilmesMaisAcessadosServlet: Resposta JSON (dados reais): " + jsonResponse);
                
                PrintWriter out = response.getWriter();
                out.print(jsonResponse);
                out.flush();
            }
        } catch (Exception e) {
            System.err.println("FilmesMaisAcessadosServlet: Erro ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar um erro ao cliente
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"erro\": \"Erro interno do servidor\"}");
            out.flush();
        }
    }
}
