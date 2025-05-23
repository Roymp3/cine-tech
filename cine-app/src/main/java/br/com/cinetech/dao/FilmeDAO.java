package br.com.cinetech.dao;

import br.com.cinetech.model.FilmeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FilmeDAO {    public boolean CreateFilme(FilmeModel Filme){
        String SQL = "INSERT INTO tb_filme (NOME, GENERO, SINOPSE, BANNER, BANNER_FIXO)" +
                                             "VALUES (?, ?, ?, ?, ?);";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, Filme.getNome());
            preparedStatement.setString(2, Filme.getGenero());
            preparedStatement.setString(3, Filme.getSinopse());
            preparedStatement.setBytes(4, Filme.getBanner());
            preparedStatement.setBytes(5, Filme.getBannerFixo());

            preparedStatement.execute();

            System.out.println("Success in insert filme");

            return true;

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            return false;
        }

    }    public boolean UpdateFilme(FilmeModel Filme){
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
                    resultSet.getBytes("BANNER_FIXO")
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
                    resultSet.getBytes("BANNER_FIXO")
                ));
            }
            
            connection.close();
            
        } catch (Exception e) {
            System.out.println("Error in listarFilmes: " + e.getMessage());
        }
        
        return filmes;
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

