package br.com.cinetech.model;

public class FilmeModel {
    private int id;
    private String nome;
    private String genero;
    private String sinopse;
    private byte[] banner;


    public FilmeModel(String nome, String genero, String sinopse, byte[] banner) {
        this.nome = nome;
        this.genero = genero;
        this.sinopse = sinopse;
        this.banner = banner;
    }


    public FilmeModel(int id, String nome, String genero, String sinopse, byte[] banner) {
        this.id = id;
        this.nome = nome;
        this.genero = genero;
        this.sinopse = sinopse;
        this.banner = banner;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public byte[] getBanner() {
        return banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }
}
