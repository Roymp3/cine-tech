package br.com.cinetech.dao;

import br.com.cinetech.model.FilmeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FilmeDAO {    
      public int CreateFilme(FilmeModel Filme){
    String SQL = "INSERT INTO tb_filme (NOME, GENERO, SINOPSE, BANNER, BANNER_FIXO, DESTAQUEDASEMANA )" +
                 "VALUES (?, ?, ?, ?, ?, ?);";

    try {
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

        System.out.println("Success in database connection");

        if (Filme.isDestaqueSemana()) {
            String sqlBuscaDestaque = "SELECT id_filme FROM tb_filme WHERE DESTAQUEDASEMANA = TRUE";
            PreparedStatement psBusca = connection.prepareStatement(sqlBuscaDestaque);
            ResultSet rs = psBusca.executeQuery();
            while (rs.next()) {
                int idDestaque = rs.getInt("id_filme");
                String sqlRemoveDestaque = "UPDATE tb_filme SET DESTAQUEDASEMANA = FALSE WHERE id_filme = ?";
                PreparedStatement psRemove = connection.prepareStatement(sqlRemoveDestaque);
                psRemove.setInt(1, idDestaque);
                psRemove.executeUpdate();
            }
        }

        PreparedStatement preparedStatement = connection.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, Filme.getNome());
        preparedStatement.setString(2, Filme.getGenero());
        preparedStatement.setString(3, Filme.getSinopse());
        preparedStatement.setBytes(4, Filme.getBanner());
        preparedStatement.setBytes(5, Filme.getBannerFixo());
        preparedStatement.setBoolean(6, Filme.isDestaqueSemana());

        int affectedRows = preparedStatement.executeUpdate();
        
        if (affectedRows > 0) {
            // Obter o ID gerado
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                Filme.setId(id); // Atualizar o ID no modelo
                System.out.println("Success in insert filme with ID: " + id);
                return id;
            }
        }
          System.out.println("Success in insert filme, but couldn't get ID");
        return -1; // Não conseguiu obter o ID

    } catch (Exception e) {
        System.out.println("Error closing connection: " + e.getMessage());
        return -1; // Retorna -1 em caso de erro
    }
}

    public boolean UpdateFilme(FilmeModel Filme){
        String SQL = "UPDATE tb_filme SET NOME = ?, GENERO = ?, SINOPSE = ?, BANNER = ?, BANNER_FIXO = ? WHERE id_filme = ?;";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, Filme.getNome());
            preparedStatement.setString(2, Filme.getGenero());
            preparedStatement.setString(3, Filme.getSinopse());
            preparedStatement.setBytes(4, Filme.getBanner());
            preparedStatement.setBytes(5, Filme.getBannerFixo());
            preparedStatement.setInt(6, Filme.getId());

            preparedStatement.execute();

            System.out.println("Success in update filme");

            return true;

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            return false;
        }

    }    public boolean DeleteFilme(int id){
        String SQL = "DELETE FROM tb_filme WHERE id_filme = ?;";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, id);

            preparedStatement.execute();

            System.out.println("Success in delete filme");

            return true;

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            return false;
        }

    }    public FilmeModel GetFilme(int id){
        String SQL = "SELECT * FROM tb_filme WHERE id_filme = ?;";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()){
                System.out.println("Success in get filme");
                return new FilmeModel(
                    resultSet.getInt("id_filme"), 
                    resultSet.getString("NOME"),  
                    resultSet.getString("GENERO"), 
                    resultSet.getString("SINOPSE"), 
                    resultSet.getBytes("BANNER"),
                    resultSet.getBytes("BANNER_FIXO"),
                    resultSet.getBoolean("DESTAQUEDASEMANA")
                );
            }

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        return null;
    }

    public java.util.List<FilmeModel> listarFilmes(String genero) {
        String SQL = "SELECT * FROM tb_filme";
        
        if (genero != null && !genero.isEmpty()) {
            SQL += " WHERE GENERO = ?";
        }
        
        java.util.List<FilmeModel> filmes = new java.util.ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            
            System.out.println("Success in database connection");
            
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            
            if (genero != null && !genero.isEmpty()) {
                preparedStatement.setString(1, genero);
            }
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                filmes.add(new FilmeModel(
                    resultSet.getInt("id_filme"),
                    resultSet.getString("NOME"),
                    resultSet.getString("GENERO"),
                    resultSet.getString("SINOPSE"),
                    resultSet.getBytes("BANNER"),
                    resultSet.getBytes("BANNER_FIXO"),
                    resultSet.getBoolean("DESTAQUEDASEMANA")

                ));
            }
            
            connection.close();
            
        } catch (Exception e) {
            System.out.println("Error in listarFilmes: " + e.getMessage());
        }
        
        return filmes;
    }

    
    /**
     * Vincula atores a um filme
     * @param filmeId ID do filme
     * @param atoresIds Lista com os IDs dos atores
     * @return true se conseguiu vincular todos, false caso contrário
     */
    public boolean vincularAtores(int filmeId, java.util.List<Integer> atoresIds) {
        if (filmeId <= 0 || atoresIds == null || atoresIds.isEmpty()) {
            return false;
        }
        
        String SQL = "INSERT INTO tb_filme_ator (id_filme, id_ator) VALUES (?, ?)";
        
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            
            // Primeiro, remover vínculos existentes
            String deleteSQL = "DELETE FROM tb_filme_ator WHERE id_filme = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, filmeId);
            deleteStmt.executeUpdate();
            
            // Adicionar os novos vínculos
            connection.setAutoCommit(false);
            
            for (Integer atorId : atoresIds) {
                preparedStatement.setInt(1, filmeId);
                preparedStatement.setInt(2, atorId);
                preparedStatement.addBatch();
            }
            
            preparedStatement.executeBatch();
            connection.commit();
            
            System.out.println("Success in linking " + atoresIds.size() + " actors to film ID " + filmeId);
            return true;
            
        } catch (Exception e) {
            System.out.println("Error linking actors to film: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca os atores associados a um filme específico
     * @param filmeId ID do filme
     * @return Lista de atores no formato [{id: 1, nome: "Nome do Ator"}, ...]
     */
    public java.util.List<java.util.Map<String, Object>> buscarAtoresDoFilme(int filmeId) {
        java.util.List<java.util.Map<String, Object>> atores = new java.util.ArrayList<>();
        
        String SQL = "SELECT a.id_ator, a.nm_ator FROM tb_ator a " +
                     "INNER JOIN tb_filme_ator fa ON a.id_ator = fa.id_ator " +
                     "WHERE fa.id_filme = ? " +
                     "ORDER BY a.nm_ator";
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            preparedStatement.setInt(1, filmeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                java.util.Map<String, Object> ator = new java.util.HashMap<>();
                ator.put("id", resultSet.getInt("id_ator"));
                ator.put("nome", resultSet.getString("nm_ator"));
                atores.add(ator);
            }
            
            System.out.println("Encontrados " + atores.size() + " atores para o filme ID: " + filmeId);
            return atores;
            
        } catch (Exception e) {
            System.out.println("Erro ao buscar atores do filme: " + e.getMessage());
            e.printStackTrace();
            return atores;
        }
    }


    public List<FilmeModel> buscarPorNome(String nome) {
        String SQL = "SELECT * FROM tb_filme WHERE NOME LIKE ?";
        List<FilmeModel> filmes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, "%" + nome + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                FilmeModel filme = new FilmeModel();
                filme.setId(rs.getInt("id_filme"));
                filme.setNome(rs.getString("NOME"));
                filme.setSinopse(rs.getString("SINOPSE"));
                filme.setGenero(rs.getString("GENERO"));
                filme.setBanner(rs.getBytes("BANNER"));
                filme.setBannerFixo(rs.getBytes("BANNER_FIXO"));
                filmes.add(filme);
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filmes;
    }


}

