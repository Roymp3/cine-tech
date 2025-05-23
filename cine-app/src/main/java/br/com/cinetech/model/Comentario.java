package br.com.cinetech.model;

import java.util.Date;

/**
 * Modelo para representar um comentário de filme
 */
public class Comentario {
    private int id;
    private int idFilme;
    private String emailUsuario;
    private String nomeUsuario;
    private String texto;
    private Date dataComentario;
    private int likes;
    private int dislikes;
    private int avaliacao; // 1-5 estrelas
    
    public Comentario() {
        this.dataComentario = new Date();
        this.likes = 0;
        this.dislikes = 0;
        this.avaliacao = 0;
    }

    public Comentario(int idFilme, String emailUsuario, String nomeUsuario, String texto, int avaliacao) {
        this.idFilme = idFilme;
        this.emailUsuario = emailUsuario;
        this.nomeUsuario = nomeUsuario;
        this.texto = texto;
        this.dataComentario = new Date();
        this.likes = 0;
        this.dislikes = 0;
        this.avaliacao = avaliacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFilme() {
        return idFilme;
    }

    public void setIdFilme(int idFilme) {
        this.idFilme = idFilme;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
    
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getDataComentario() {
        return dataComentario;
    }

    public void setDataComentario(Date dataComentario) {
        this.dataComentario = dataComentario;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
    
    public int getAvaliacao() {
        return avaliacao;
    }
    
    public void setAvaliacao(int avaliacao) {
        if (avaliacao >= 0 && avaliacao <= 5) {
            this.avaliacao = avaliacao;
        }
    }
}
