package br.com.cinetech.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para controle de acessos a filmes
 */
public class FilmeAcessoDAO {
    
    /**
     * Construtor que testa a conexão e cria a tabela se necessário
     */
    public FilmeAcessoDAO() {
        try {
            testarConexao();
            criarTabelaSeNaoExistir();
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar FilmeAcessoDAO: " + e.getMessage());
        }
    }
    
    /**
     * Testa a conexão com o banco de dados
     */
    private void testarConexao() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa")) {
            if (connection != null) {
                System.out.println("Conexão com banco de dados estabelecida com sucesso.");
            }
        }
    }
    
    /**
     * Registra um acesso ao filme
     * @param idFilme ID do filme acessado
     * @param ipUsuario endereço IP do usuário (opcional)
     * @return true se foi registrado com sucesso
     */
    public boolean registrarAcesso(int idFilme, String ipUsuario) {
        String SQL = "INSERT INTO tb_filme_acesso (id_filme, ip_usuario) VALUES (?, ?)";
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = connection.prepareStatement(SQL)) {
            
            stmt.setInt(1, idFilme);
            stmt.setString(2, ipUsuario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar acesso ao filme: " + e.getMessage());
            
            // Verificar se a tabela existe, se não, tentamos criá-la
            try {
                criarTabelaSeNaoExistir();
                
                // Tentar novamente após criar a tabela
                try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
                     PreparedStatement stmt = connection.prepareStatement(SQL)) {
                    
                    stmt.setInt(1, idFilme);
                    stmt.setString(2, ipUsuario);
                    
                    int linhasAfetadas = stmt.executeUpdate();
                    return linhasAfetadas > 0;
                }
            } catch (SQLException ex) {
                System.err.println("Erro ao criar tabela e registrar acesso: " + ex.getMessage());
                return false;
            }
        }
        // Este código nunca será alcançado, mas é mantido como proteção extra
        // return false;
    }
    
    /**
     * Lista os filmes mais acessados
     * @param ordem "maior" para ordenar do mais acessado para o menos, "menor" para o inverso
     * @param limite número máximo de filmes a retornar
     * @return lista de filmes mais acessados com suas contagens
     */
    public List<Map<String, Object>> listarFilmesMaisAcessados(String ordem, int limite) {
        System.out.println("Listando filmes mais acessados, ordem: " + ordem + ", limite: " + limite);
        
        // Usar LEFT JOIN para incluir filmes que ainda não foram acessados
        String SQL = "SELECT f.nome, COUNT(fa.id_acesso) as acessos " +
                     "FROM tb_filme f " +
                     "LEFT JOIN tb_filme_acesso fa ON f.id_filme = fa.id_filme " +
                     "GROUP BY f.nome " +
                     "ORDER BY acessos " + ("maior".equalsIgnoreCase(ordem) ? "DESC" : "ASC") + " " +
                     "LIMIT ?";
        
        List<Map<String, Object>> filmes = new ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = connection.prepareStatement(SQL)) {
            
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> filme = new HashMap<>();
                filme.put("nome", rs.getString("nome"));
                filme.put("acessos", rs.getInt("acessos"));
                filmes.add(filme);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar filmes mais acessados: " + e.getMessage());
            
            // Tentar tratar a causa do erro
            if (e.getMessage().contains("Table \"TB_FILME_ACESSO\" not found")) {
                System.out.println("Tabela tb_filme_acesso não encontrada, tentando criar...");
                try {
                    criarTabelaSeNaoExistir();
                    System.out.println("Tabela criada com sucesso.");
                    
                    // Tentar listar filmes sem considerar acessos
                    try {
                        String sqlAlternativo = "SELECT nome, 0 as acessos FROM tb_filme LIMIT ?";
                        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
                             PreparedStatement stmt = connection.prepareStatement(sqlAlternativo)) {
                            
                            stmt.setInt(1, limite);
                            ResultSet rs = stmt.executeQuery();
                            
                            while (rs.next()) {
                                Map<String, Object> filme = new HashMap<>();
                                filme.put("nome", rs.getString("nome"));
                                filme.put("acessos", 0);
                                filmes.add(filme);
                            }
                            
                            System.out.println("Recuperados " + filmes.size() + " filmes da tabela tb_filme");
                        }
                    } catch (SQLException ex) {
                        System.err.println("Erro ao buscar filmes após criar tabela de acessos: " + ex.getMessage());
                    }
                } catch (SQLException ex) {
                    System.err.println("Erro ao criar tabela de acessos: " + ex.getMessage());
                }
            }
        }
        
        return filmes;
    }
    
    /**
     * Cria a tabela de acessos se ainda não existir
     */
    private void criarTabelaSeNaoExistir() throws SQLException {
        System.out.println("Tentando criar tabela tb_filme_acesso...");
        
        String SQL = "CREATE TABLE IF NOT EXISTS tb_filme_acesso (" +
                     "id_acesso INT PRIMARY KEY AUTO_INCREMENT, " +
                     "id_filme INT NOT NULL, " +
                     "data_acesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "ip_usuario VARCHAR(50)" +
                     ")";
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.execute();
            
            // Tenta adicionar a foreign key se a tabela já não tiver sido criada com ela
            try {
                String SQL_FK = "ALTER TABLE tb_filme_acesso ADD CONSTRAINT fk_filme_acesso " +
                               "FOREIGN KEY (id_filme) REFERENCES tb_filme(id_filme)";
                PreparedStatement stmtFK = connection.prepareStatement(SQL_FK);
                stmtFK.execute();
                stmtFK.close();
            } catch (SQLException e) {
                // Ignora erro se a constraint já existir
                if (!e.getMessage().contains("already exists")) {
                    System.err.println("Aviso: Não foi possível adicionar foreign key: " + e.getMessage());
                }
            }
            
            // Adicionar índice para melhorar as consultas
            try {
                String SQL_INDEX = "CREATE INDEX IF NOT EXISTS idx_filme_acesso ON tb_filme_acesso(id_filme)";
                PreparedStatement stmtIndex = connection.prepareStatement(SQL_INDEX);
                stmtIndex.execute();
                stmtIndex.close();
            } catch (SQLException e) {
                System.err.println("Aviso: Não foi possível criar índice: " + e.getMessage());
            }
        }
    }
}
