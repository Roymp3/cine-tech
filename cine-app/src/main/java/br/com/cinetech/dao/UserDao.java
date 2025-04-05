package br.com.cinetech.dao;

import br.com.cinetech.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserDao {

    public void CreateUser(User user) {

        String SQL = "INSERT INTO tb_usuario (nm_pessoa, nm_usuario, ds_senha, nr_telefone, ds_email) \n" +
                "VALUES (?, ?, ?, ?,?);";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa","sa");

            System.out.println("Sucess in databsae connection");


            PreparedStatement preparedStatement =  connection.prepareStatement(SQL);


            preparedStatement.setString(1, user.getNm_pessoa());
            preparedStatement.setString(2, user.getNm_usuario());
            preparedStatement.setString(3, user.getSenha());
            preparedStatement.setString(4, user.getTelefone());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.execute();

            System.out.println("Sucess in insert user");


        } catch (Exception e) {


            System.out.println("Error closing connection: " + e.getMessage());
        }

    }
}
