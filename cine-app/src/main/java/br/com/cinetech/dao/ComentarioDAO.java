package br.com.cinetech.dao;

import br.com.cinetech.model.Comentario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com comentários de filmes
 */
public class ComentarioDAO {
    
    /**
     * Construtor que inicializa a tabela se necessário
     */
    public ComentarioDAO() {
        try {
            criarTabelaSeNaoExistir();
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar ComentarioDAO: " + e.getMessage());
        }
    }
    
    /**
     * Cria a tabela de comentários se não existir
     */
    private void criarTabelaSeNaoExistir() throws SQLException {
        System.out.println("Verificando tabela de comentários...");
        
        // Primeiro, tenta verificar se a tabela existe e tem a coluna DATA_COMENTARIO
        boolean needsRecreation = false;
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             Statement stmt = conn.createStatement()) {
            
            // Verifica se a tabela existe
            try {
                ResultSet rs = stmt.executeQuery("SELECT * FROM TB_COMENTARIO LIMIT 1");
                rs.close();
            } catch (SQLException e) {
                // Tabela não existe, precisamos criar
                needsRecreation = true;
            }
            
            // Se a tabela existe, verifica se tem a coluna DATA_COMENTARIO
            if (!needsRecreation) {
                try {
                    ResultSet rs = stmt.executeQuery("SELECT DATA_COMENTARIO FROM TB_COMENTARIO LIMIT 1");
                    rs.close();
                } catch (SQLException e) {
                    // Coluna não existe ou tem outro nome, precisamos recriar
                    System.out.println("Coluna DATA_COMENTARIO não encontrada, recriando tabela...");
                    needsRecreation = true;
                }
            }
            
            // Se precisamos recriar a tabela, fazemos isso
            if (needsRecreation) {
                // Drop table if exists
                stmt.execute("DROP TABLE IF EXISTS TB_COMENTARIO");
                
                String sql = "CREATE TABLE IF NOT EXISTS TB_COMENTARIO (" +
                         "ID_COMENTARIO INT AUTO_INCREMENT PRIMARY KEY, " +
                         "ID_FILME INT NOT NULL, " +
                         "EMAIL_USUARIO VARCHAR(255) NOT NULL, " +
                         "NOME_USUARIO VARCHAR(255) NOT NULL, " +
                         "TEXTO TEXT NOT NULL, " +
                         "DATA_COMENTARIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                         "LIKES INT DEFAULT 0, " +
                         "DISLIKES INT DEFAULT 0, " +
                         "AVALIACAO INT DEFAULT 0)";
                
                stmt.execute(sql);
                System.out.println("Tabela de comentários recriada com sucesso");
            } else {
                System.out.println("Tabela de comentários já existe e parece estar correta");
            }
            
            // Verificar índices
            try {
                stmt.execute("CREATE INDEX IF NOT EXISTS IDX_COMENTARIO_FILME ON TB_COMENTARIO(ID_FILME)");
                stmt.execute("CREATE INDEX IF NOT EXISTS IDX_COMENTARIO_USUARIO ON TB_COMENTARIO(EMAIL_USUARIO)");
            } catch (SQLException e) {
                System.out.println("Aviso ao criar índices: " + e.getMessage());
            }
            
            // Tentar criar a relação se TB_FILME existir
            try {
                stmt.execute("ALTER TABLE TB_COMENTARIO ADD CONSTRAINT IF NOT EXISTS FK_COMENTARIO_FILME " +
                             "FOREIGN KEY (ID_FILME) REFERENCES TB_FILME(ID_FILME)");
            } catch (SQLException e) {
                System.out.println("Aviso ao criar chave estrangeira: " + e.getMessage());
            }
        }
    }
    
    /**
     * Adiciona um novo comentário
     * @param comentario Objeto comentário a ser adicionado
     * @return ID do comentário adicionado ou -1 em caso de erro
     */
    public int adicionarComentario(Comentario comentario) {
        String sql = "INSERT INTO TB_COMENTARIO (ID_FILME, EMAIL_USUARIO, NOME_USUARIO, TEXTO, AVALIACAO) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        System.out.println("Inserindo comentário: idFilme=" + comentario.getIdFilme() + 
                         ", email=" + comentario.getEmailUsuario() + 
                         ", nome=" + comentario.getNomeUsuario() + 
                         ", avaliacao=" + comentario.getAvaliacao());
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            conn.setAutoCommit(false);
            
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, comentario.getIdFilme());
            stmt.setString(2, comentario.getEmailUsuario());
            stmt.setString(3, comentario.getNomeUsuario());
            stmt.setString(4, comentario.getTexto());
            stmt.setInt(5, comentario.getAvaliacao());
            
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("Linhas afetadas pelo INSERT: " + linhasAfetadas);
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        conn.commit();
                        System.out.println("Comentário inserido com sucesso, ID gerado: " + id);
                        return id;
                    }
                }
            }
            
            // Se chegou aqui, é porque não conseguiu obter o ID
            conn.rollback();
            System.err.println("Não foi possível obter o ID gerado para o comentário.");
            
        } catch (SQLException e) {
            System.err.println("Erro SQL ao adicionar comentário: " + e.getMessage());
            e.printStackTrace();
            
            // Tenta fazer a verificação da tabela e recriar se necessário
            try {
                if (conn != null) conn.rollback();
                
                // Verifica se o erro é relacionado à estrutura da tabela
                if (e.getMessage().contains("Column") || e.getMessage().contains("Table")) {
                    System.out.println("Tentando recriar a tabela...");
                    criarTabelaSeNaoExistir();
                    
                    // Tentar novamente após recriar a tabela
                    return adicionarComentarioRetry(comentario);
                }
            } catch (SQLException ex) {
                System.err.println("Erro ao tentar recuperar de falha: " + ex.getMessage());
            }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        
        return -1;
    }
    
    /**
     * Tenta adicionar um comentário novamente após recriar a tabela
     */
    private int adicionarComentarioRetry(Comentario comentario) {
        String sql = "INSERT INTO TB_COMENTARIO (ID_FILME, EMAIL_USUARIO, NOME_USUARIO, TEXTO, AVALIACAO) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        System.out.println("Tentativa de reinserção após recriar tabela...");
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, comentario.getIdFilme());
            stmt.setString(2, comentario.getEmailUsuario());
            stmt.setString(3, comentario.getNomeUsuario());
            stmt.setString(4, comentario.getTexto());
            stmt.setInt(5, comentario.getAvaliacao());
            
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro na segunda tentativa de adicionar comentário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Lista todos os comentários de um filme
     * @param idFilme ID do filme
     * @return Lista de comentários
     */
    public List<Comentario> listarComentariosPorFilme(int idFilme) {
        List<Comentario> comentarios = new ArrayList<>();
        // Usar colunas em maiúsculas para evitar problemas de case-sensitivity
        String sql = "SELECT * FROM TB_COMENTARIO WHERE ID_FILME = ? ORDER BY DATA_COMENTARIO DESC";
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFilme);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comentario comentario = new Comentario();
                    comentario.setId(rs.getInt("ID_COMENTARIO"));
                    comentario.setIdFilme(rs.getInt("ID_FILME"));
                    comentario.setEmailUsuario(rs.getString("EMAIL_USUARIO"));
                    comentario.setNomeUsuario(rs.getString("NOME_USUARIO"));
                    comentario.setTexto(rs.getString("TEXTO"));
                    comentario.setDataComentario(rs.getTimestamp("DATA_COMENTARIO"));
                    comentario.setLikes(rs.getInt("LIKES"));
                    comentario.setDislikes(rs.getInt("DISLIKES"));
                    comentario.setAvaliacao(rs.getInt("AVALIACAO"));
                    
                    comentarios.add(comentario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar comentários: " + e.getMessage());
            e.printStackTrace();
        }
        
        return comentarios;
    }
    
    /**
     * Atualiza likes ou dislikes de um comentário
     * @param idComentario ID do comentário
     * @param likes Quantidade de likes (quando positivo) ou dislikes (quando negativo)
     * @return true se atualizado com sucesso
     */
    public boolean atualizarReacao(int idComentario, int likes) {
        String sql;
        if (likes > 0) {
            sql = "UPDATE TB_COMENTARIO SET LIKES = LIKES + 1 WHERE ID_COMENTARIO = ?";
        } else {
            sql = "UPDATE TB_COMENTARIO SET DISLIKES = DISLIKES + 1 WHERE ID_COMENTARIO = ?";
        }
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idComentario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar reação: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Verifica se um usuário já comentou em um filme
     * @param idFilme ID do filme
     * @param emailUsuario Email do usuário
     * @return true se o usuário já comentou neste filme
     */
    public boolean usuarioJaComentou(int idFilme, String emailUsuario) {
        String sql = "SELECT COUNT(*) FROM TB_COMENTARIO WHERE ID_FILME = ? AND EMAIL_USUARIO = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFilme);
            stmt.setString(2, emailUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar se usuário já comentou: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Exclui um comentário
     * @param idComentario ID do comentário
     * @param emailUsuario Email do usuário (para verificar permissão)
     * @return true se excluído com sucesso
     */
    public boolean excluirComentario(int idComentario, String emailUsuario) {
        String sql = "DELETE FROM TB_COMENTARIO WHERE ID_COMENTARIO = ? AND EMAIL_USUARIO = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idComentario);
            stmt.setString(2, emailUsuario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir comentário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Exclui um comentário pelo administrador
     * @param idComentario ID do comentário
     * @return true se excluído com sucesso
     */
    public boolean excluirComentarioAdmin(int idComentario) {
        String sql = "DELETE FROM TB_COMENTARIO WHERE ID_COMENTARIO = ?";
        
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idComentario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir comentário como admin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
