package br.com.cinetech.dao;

import br.com.cinetech.model.Movie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MovieDao {
    public void CreateMovie(Movie movie){

        String SQL = "INSERT INTO MOVIE(NAME) VALUES(?)";

        try {
         Connection connection =  DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            System.out.println("Sucess in databsae connection");


            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, movie.getName());
            preparedStatement.execute();

            System.out.println("Sucess in insert movie");

            connection.close();
        }catch (Exception e){

            System.out.println("falha ao conncetar no databsae");
        }

    }

}
