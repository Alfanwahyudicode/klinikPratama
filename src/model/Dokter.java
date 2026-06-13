/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author VanZ
 */
public class Dokter {
 
    private int idDokter;
    private String namaDokter;
    private String spesialisasi;
    private String noTelp;

    public Dokter() {}

    public Dokter(int idDokter, String namaDokter, String spesialisasi, String noTelp) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.spesialisasi = spesialisasi;
        this.noTelp = noTelp;
    }

    public int getIdDokter() { return idDokter; }
    public void setIdDokter(int idDokter) { this.idDokter = idDokter; }

    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }

    public String getSpesialisasi() { return spesialisasi; }
    public void setSpesialisasi(String spesialisasi) { this.spesialisasi = spesialisasi; }

    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
}

