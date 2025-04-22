package br.com.cinetech.servlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import br.com.cinetech.dao.FilmeDAO;
import br.com.cinetech.model.FilmeModel;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

@WebServlet("/cadastrarFilme")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // 5MB
public class FilmeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String genero = request.getParameter("genero");
        String sinopse = request.getParameter("sinopse");

        Part filePart = request.getPart("banner");
        byte[] banner = extractBytesFromInputStream(filePart.getInputStream());

        FilmeModel filme = new FilmeModel(nome, genero, sinopse, banner);

        try {
            FilmeDAO filmeDao = new FilmeDAO();
            filmeDao.CreateFilme(filme);

            response.sendRedirect("paginaFilmes.html");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            response.sendError(500, "Erro ao cadastrar o filme.");
        }
    }

    private byte[] extractBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];  // Buffer de 1 KB
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
