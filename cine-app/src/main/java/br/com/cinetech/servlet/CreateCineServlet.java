package br.com.cinetech.servlet;

import br.com.cinetech.dao.MovieDao;
import br.com.cinetech.model.Movie;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/create-cine")
public class CreateCineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("cine-name");
        Movie movie = new Movie();
        movie.setName(name);

            MovieDao moviedao = new MovieDao();
            moviedao.CreateMovie(movie);

        request.getRequestDispatcher("index.html").forward(request, response);


    }
}
