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

    public boolean VerifyUser(User user){

        String SQL = "SELECT * FROM tb_usuario WHERE nm_usuario = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, user.getNm_usuario());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Usuario encontrado");
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error ao encontrar usuario: " + e.getMessage());
        }
        return false;
    }
    public boolean verifyEmail(User user){

        String SQL = "SELECT * FROM tb_usuario WHERE ds_email = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, user.getEmail());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("email encontrado");
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error  ao encontrar email : " + e.getMessage());
        }
        return false;
    }

    public boolean verifyNumber(User user){

        String SQL = "SELECT * FROM tb_usuario WHERE nr_telefone = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, user.getTelefone());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("telefone encontrado");
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error  ao encontrar telefone : " + e.getMessage());
        }
        return false;
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

    public String getUserName(User user) {
        String SQL = "SELECT nm_usuario FROM tb_usuario WHERE ds_email = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            System.out.println("Success in database connection");

            preparedStatement.setString(1, user.getEmail());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("usuario encontardo");
                    return resultSet.getString("nm_usuario");
                }
            }

        } catch (Exception e) {
            System.out.println("Error ao encontrar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca um usuário pelo email
     * @param email Email do usuário
     * @return Objeto User ou null se não encontrado
     */
    public User buscarPorEmail(String email) {
        String SQL = "SELECT * FROM tb_usuario WHERE ds_email = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setEmail(resultSet.getString("ds_email"));
                    user.setNm_usuario(resultSet.getString("nm_usuario"));
                    user.setNm_pessoa(resultSet.getString("nm_pessoa"));
                    // Não setamos a senha por segurança
                    user.setTelefone(resultSet.getString("nr_telefone"));
                    return user;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar usuário por email: " + e.getMessage());
        }
        return null;
    }
}
