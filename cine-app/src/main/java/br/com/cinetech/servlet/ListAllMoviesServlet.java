//package br.com.cinetech.servlet;
//
//import br.com.cinetech.dao.MovieDao;
//import br.com.cinetech.model.Movie;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.rmi.MarshalledObject;
//import java.util.List;
//
//@WebServlet("/find-all-movies")
//public class ListAllMoviesServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Movie> movies = new MovieDao().findAllMovies();
//
//        request.setAttribute("movies", movies);
//        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
//
//    }
//
//
//}
