package br.com.cinetech.dao;

import br.com.cinetech.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {

    public void CreateUser(User user) {

        String SQL = "INSERT INTO tb_usuario (nm_pessoa, nm_usuario, ds_senha, nr_telefone, ds_email) \n" +
                "VALUES (?, ?, ?, ?, ?);";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");

            System.out.println("Success in database connection");

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, user.getNm_pessoa());
            preparedStatement.setString(2, user.getNm_usuario());
            preparedStatement.setString(3, user.getSenha());
            preparedStatement.setString(4, user.getTelefone());
            preparedStatement.setString(5, user.getEmail());

            preparedStatement.execute();

            System.out.println("Success in insert user");

        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean CheckLogin(User user) {
        String SQL = "SELECT * FROM tb_usuario WHERE ds_email = ? AND ds_senha = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getSenha());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Usuario encontrado");
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error na validação do login: " + e.getMessage());
        }
        return false;
    }
}