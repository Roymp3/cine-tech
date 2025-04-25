package br.com.cinetech.dao;

import br.com.cinetech.model.FilmeModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FilmeDAO {
    public boolean CreateFilme(FilmeModel Filme){
        String SQL = "INSERT INTO tb_filme (NOME, GENERO, SINOPSE, BANNER)" +
                                             "VALUES (?, ?, ?, ?);";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, Filme.getNome());
            preparedStatement.setString(2, Filme.getSinopse());
            preparedStatement.setString(3, Filme.getGenero());
            preparedStatement.setBytes(4, Filme.getBanner());

            preparedStatement.execute();

            System.out.println("Success in insert filme");

            return true;

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            return false;
        }

    }
    public boolean UpdateFilme(FilmeModel Filme){
        String SQL = "UPDATE tb_filme SET nm_filme = ?, ds_sinopse = ?, ds_genero = ?, ds_banner = ? WHERE id_filme = ?;";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, Filme.getNome());
            preparedStatement.setString(2, Filme.getSinopse());
            preparedStatement.setString(3, Filme.getGenero());
            preparedStatement.setBytes(4, Filme.getBanner());
            preparedStatement.setInt(5, Filme.getId());

            preparedStatement.execute();

            System.out.println("Success in update filme");

            return true;

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
            return false;
        }

    }

    public boolean DeleteFilme(int id){
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

    }

    public FilmeModel GetFilme(int id){
        String SQL = "SELECT * FROM tb_filme WHERE id_filme = ?;";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                System.out.println("Success in get filme");
                return new FilmeModel(resultSet.getInt("id_filme"), resultSet.getString("nm_filme"), resultSet.getString("ds_genero"), resultSet.getString("ds_sinopse"), resultSet.getBytes("ds_banner"));
            }

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        return null;
    }
}
