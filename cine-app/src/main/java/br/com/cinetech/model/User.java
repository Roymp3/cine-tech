package br.com.cinetech.model;

public class User {

    private String nm_pessoa;
    private String nm_usuario;
    private String senha;
    private String telefone;
    private String email;


    public String getNm_pessoa() {
        return nm_pessoa;
    }

    public void setNm_pessoa(String nm_pessoa) {
        this.nm_pessoa = nm_pessoa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNm_usuario() {
        return nm_usuario;
    }

    public void setNm_usuario(String nm_usuario) {
        this.nm_usuario = nm_usuario;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public User(String nm_pessoa, String nm_usuario, String senha, String telefone, String email ){
        this.nm_pessoa = nm_pessoa;
        this.nm_usuario = nm_usuario;
        this.senha = senha;
        this.telefone = telefone;
        this.email = email;

    }

    public User(){
    }
}
