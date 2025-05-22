package br.com.cinetech.model;

import java.io.InputStream;
import java.sql.Date;

public class AtorModel {
    private int idAtor;
    private String nmAtor;
    private String dsBiografia;
    private Date dtNascimento;
    private String nmNacionalidade;
    private String dsPremios;
    private String dsFilmesFamosos;
    private InputStream imgFoto;
    private byte[] fotoBytes;

    public AtorModel() {
    }

    public AtorModel(String nmAtor, String dsBiografia, Date dtNascimento, 
                    String nmNacionalidade, String dsPremios, String dsFilmesFamosos) {
        this.nmAtor = nmAtor;
        this.dsBiografia = dsBiografia;
        this.dtNascimento = dtNascimento;
        this.nmNacionalidade = nmNacionalidade;
        this.dsPremios = dsPremios;
        this.dsFilmesFamosos = dsFilmesFamosos;
    }

    public AtorModel(int idAtor, String nmAtor, String dsBiografia, Date dtNascimento, 
                    String nmNacionalidade, String dsPremios, String dsFilmesFamosos) {
        this.idAtor = idAtor;
        this.nmAtor = nmAtor;
        this.dsBiografia = dsBiografia;
        this.dtNascimento = dtNascimento;
        this.nmNacionalidade = nmNacionalidade;
        this.dsPremios = dsPremios;
        this.dsFilmesFamosos = dsFilmesFamosos;
    }

    // Getters e Setters
    public int getIdAtor() {
        return idAtor;
    }

    public void setIdAtor(int idAtor) {
        this.idAtor = idAtor;
    }

    public String getNmAtor() {
        return nmAtor;
    }

    public void setNmAtor(String nmAtor) {
        this.nmAtor = nmAtor;
    }

    public String getDsBiografia() {
        return dsBiografia;
    }

    public void setDsBiografia(String dsBiografia) {
        this.dsBiografia = dsBiografia;
    }

    public Date getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public String getNmNacionalidade() {
        return nmNacionalidade;
    }

    public void setNmNacionalidade(String nmNacionalidade) {
        this.nmNacionalidade = nmNacionalidade;
    }

    public String getDsPremios() {
        return dsPremios;
    }

    public void setDsPremios(String dsPremios) {
        this.dsPremios = dsPremios;
    }

    public String getDsFilmesFamosos() {
        return dsFilmesFamosos;
    }

    public void setDsFilmesFamosos(String dsFilmesFamosos) {
        this.dsFilmesFamosos = dsFilmesFamosos;
    }

    public InputStream getImgFoto() {
        return imgFoto;
    }

    public void setImgFoto(InputStream imgFoto) {
        this.imgFoto = imgFoto;
    }
    
    public byte[] getFotoBytes() {
        return fotoBytes;
    }

    public void setFotoBytes(byte[] fotoBytes) {
        this.fotoBytes = fotoBytes;
    }

    @Override
    public String toString() {
        return "AtorModel{" +
                "idAtor=" + idAtor +
                ", nmAtor='" + nmAtor + '\'' +
                ", dsBiografia='" + dsBiografia + '\'' +
                ", dtNascimento=" + dtNascimento +
                ", nmNacionalidade='" + nmNacionalidade + '\'' +
                ", dsPremios='" + dsPremios + '\'' +
                ", dsFilmesFamosos='" + dsFilmesFamosos + '\'' +
                '}';
    }
}
