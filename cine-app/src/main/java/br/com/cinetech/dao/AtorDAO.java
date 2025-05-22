package br.com.cinetech.dao;

import br.com.cinetech.model.AtorModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AtorDAO {
    
    public AtorDAO() {
    }
    public boolean inserir(AtorModel ator) {
        String SQL = "INSERT INTO tb_ator (nm_ator, ds_biografia, dt_nascimento, nm_nacionalidade, ds_premios, ds_filmes_famosos, img_foto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("Iniciando inserção de ator no banco de dados");
        System.out.println("Nome do ator: " + ator.getNmAtor());
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            System.out.println("Conexão com o banco estabelecida com sucesso");
              System.out.println("Preenchendo campos da query");
            preparedStatement.setString(1, ator.getNmAtor());
            preparedStatement.setString(2, ator.getDsBiografia());
            
            if (ator.getDtNascimento() != null) {
                preparedStatement.setDate(3, ator.getDtNascimento());
                System.out.println("Data de nascimento definida: " + ator.getDtNascimento());
            } else {
                preparedStatement.setNull(3, Types.DATE);
                System.out.println("Data de nascimento definida como NULL");
            }
            
            preparedStatement.setString(4, ator.getNmNacionalidade());
            preparedStatement.setString(5, ator.getDsPremios());
            preparedStatement.setString(6, ator.getDsFilmesFamosos());
            
            System.out.println("Processando imagem do ator");
            if (ator.getImgFoto() != null) {
                System.out.println("Usando InputStream para a imagem");
                preparedStatement.setBlob(7, ator.getImgFoto());
            } else if (ator.getFotoBytes() != null) {
                System.out.println("Usando array de bytes para a imagem, tamanho: " + ator.getFotoBytes().length);
                preparedStatement.setBlob(7, new ByteArrayInputStream(ator.getFotoBytes()));
            } else {
                System.out.println("Imagem não fornecida, definindo como NULL");
                preparedStatement.setNull(7, Types.BLOB);
            }
            
            System.out.println("Executando a query");
            preparedStatement.execute();
            
            System.out.println("Ator inserido com sucesso no banco de dados");
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro SQL ao inserir ator: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Erro inesperado ao inserir ator: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public AtorModel buscarPorId(int id) {
        String SQL = "SELECT * FROM tb_ator WHERE id_ator = ?";
        
        System.out.println("Buscando ator por ID: " + id);
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            System.out.println("Conexão com banco de dados estabelecida");
            
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    AtorModel ator = extrairAtorDoResultSet(resultSet);
                    System.out.println("Ator encontrado com sucesso: " + ator.getNmAtor());
                    return ator;
                } else {
                    System.out.println("Nenhum ator encontrado com ID: " + id);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Erro SQL ao buscar ator por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("Erro inesperado ao buscar ator por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
  
    public List<AtorModel> buscarPorNome(String nome) {
        String SQL = "SELECT * FROM tb_ator WHERE nm_ator LIKE ?";
        List<AtorModel> atores = new ArrayList<>();
        
        System.out.println("Buscando atores por nome: " + nome);
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            
            System.out.println("Conexão com banco de dados estabelecida");
            
            preparedStatement.setString(1, "%" + nome + "%");
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    AtorModel ator = extrairAtorDoResultSet(resultSet);
                    atores.add(ator);
                    System.out.println("Ator encontrado: " + ator.getNmAtor());
                }
            }
            
            System.out.println("Busca finalizada. Total de atores encontrados: " + atores.size());
            return atores;
            
        } catch (SQLException e) {
            System.out.println("Erro SQL ao buscar atores por nome: " + e.getMessage());
            e.printStackTrace();
            return atores;
        } catch (Exception e) {
            System.out.println("Erro inesperado ao buscar atores por nome: " + e.getMessage());
            e.printStackTrace();
            return atores;
        }
    }
  
     */
    public List<AtorModel> listarTodos() {
        String SQL = "SELECT * FROM tb_ator";
        List<AtorModel> atores = new ArrayList<>();
        
        System.out.println("Listando todos os atores");
        
        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            System.out.println("Conexão com banco de dados estabelecida");
            
            while (resultSet.next()) {
                AtorModel ator = extrairAtorDoResultSet(resultSet);
                atores.add(ator);
                System.out.println("Ator carregado: " + ator.getNmAtor());
            }
            
            System.out.println("Listagem finalizada. Total de atores: " + atores.size());
            return atores;
            
        } catch (SQLException e) {
            System.out.println("Erro SQL ao listar atores: " + e.getMessage());
            e.printStackTrace();
            return atores;
        } catch (Exception e) {
            System.out.println("Erro inesperado ao listar atores: " + e.getMessage());
            e.printStackTrace();
            return atores;
        }
    }
      public void fecharConexao() {
    }
  
    private AtorModel extrairAtorDoResultSet(ResultSet resultSet) throws SQLException {
        AtorModel ator = new AtorModel();
        
        ator.setIdAtor(resultSet.getInt("id_ator"));
        ator.setNmAtor(resultSet.getString("nm_ator"));
        ator.setDsBiografia(resultSet.getString("ds_biografia"));
        ator.setDtNascimento(resultSet.getDate("dt_nascimento"));
        ator.setNmNacionalidade(resultSet.getString("nm_nacionalidade"));
        ator.setDsPremios(resultSet.getString("ds_premios"));
        ator.setDsFilmesFamosos(resultSet.getString("ds_filmes_famosos"));
        

        Blob blob = resultSet.getBlob("img_foto");
        if (blob != null) {
            try {
                ator.setImgFoto(blob.getBinaryStream());
            } catch (SQLException e) {
                System.out.println("Erro ao obter stream binário da imagem: " + e.getMessage());
            }
        }
        
        return ator;
    }
}
