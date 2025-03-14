package br.com.cinetech.dao;

import br.com.cinetech.model.Movie;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieDao {

    public void CreateMovie(Movie movie) {

        String SQL = "INSERT INTO movie(name) VALUES(?)";

        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa","sa");

            System.out.println("Sucess in databsae connection");


            PreparedStatement preparedStatement =  connection.prepareStatement(SQL);
            preparedStatement.setString(1, movie.getName());
            preparedStatement.execute();

            System.out.println("Sucess in insert movie");


        } catch (Exception e) {


            System.out.println("Error closing connection: " + e.getMessage());
        }

    }

    public List<Movie> findAllMovies() {


        String sql = "SELECT * FROM MOVIE";


        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
            System.out.println("Sucess in databsae connection");

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Movie>  moveis = new ArrayList<>();

            while (resultSet.next()) {

                String moviename = resultSet.getString("name");

                Movie movie = new Movie(moviename);

                moveis.add(movie);
            }
            System.out.println("success in select * movies");

            connection.close();
            return moveis;

        } catch (Exception e) {

            System.out.println("falha ao conncetar no databsae");

            return Collections.emptyList();
        }

    }
}
