package br.com.cinetech.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.cinetech.dao.AtorDAO;
import br.com.cinetech.dao.FilmeDAO;

/**
 * Servlet para fornecer estatísticas gerais para o dashboard administrativo
 */
@WebServlet("/estatisticas")
public class EstatisticasServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("EstatisticasServlet: Recebido request GET de " + request.getRemoteAddr());
        System.out.println("EstatisticasServlet: URL completa: " + request.getRequestURL() + "?" + request.getQueryString());
        
        // Configurar cabeçalhos CORS de forma mais permissiva
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "86400");
        
        // Se for uma requisição OPTIONS (preflight), apenas retornar os cabeçalhos
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        // Configurar tipo de conteúdo para JSON
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        int filmesCadastrados = 0;
        int atoresCadastrados = 0;
        int usuariosCadastrados = 0;
        
        try {
            System.out.println("EstatisticasServlet: Contando registros...");
            
            // Contar filmes
            filmesCadastrados = contarRegistros("tb_filme", "id_filme");
            System.out.println("EstatisticasServlet: Filmes contados: " + filmesCadastrados);
            
            // Contar atores
            atoresCadastrados = contarRegistros("tb_ator", "id_ator");
            System.out.println("EstatisticasServlet: Atores contados: " + atoresCadastrados);
            
            // Contar usuários (se houver tabela de usuários)
            try {
                usuariosCadastrados = contarRegistros("tb_usuario", "id_usuario");
                System.out.println("EstatisticasServlet: Usuários contados: " + usuariosCadastrados);
            } catch (Exception e) {
                System.out.println("Tabela de usuários não encontrada ou erro ao consultar: " + e.getMessage());
                usuariosCadastrados = 0;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao contar registros: " + e.getMessage());
        }
        
        // Construir objeto JSON com as estatísticas
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"filmesCadastrados\":").append(filmesCadastrados).append(",");
        jsonBuilder.append("\"atoresCadastrados\":").append(atoresCadastrados).append(",");
        jsonBuilder.append("\"usuariosCadastrados\":").append(usuariosCadastrados).append(",");
        jsonBuilder.append("\"timestamp\":\"").append(new java.util.Date()).append("\",");
        jsonBuilder.append("\"servletPath\":\"").append(request.getServletPath()).append("\"");
        jsonBuilder.append("}");
        
        String jsonResponse = jsonBuilder.toString();
        System.out.println("EstatisticasServlet: Resposta JSON: " + jsonResponse);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
    
    /**
     * Conta registros em uma tabela
     * @param tabela nome da tabela
     * @param campoId nome do campo de ID
     * @return quantidade de registros
     */
    private int contarRegistros(String tabela, String campoId) {
        String SQL = "SELECT COUNT(" + campoId + ") FROM " + tabela;
        int count = 0;
        
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            PreparedStatement stmt = connection.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
            rs.close();
            stmt.close();
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("Erro ao contar registros em " + tabela + ": " + e.getMessage());
        }
        
        return count;
    }
}
