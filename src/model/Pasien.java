/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author VanZ
 */
public class Pasien {

    private int idPasien;
    private String namaPasien;
    private String jk;
    private String tglLahir;
    private String alamat;
    private String noTelp;

    public Pasien() {
    }

    public Pasien(int idPasien, String namaPasien, String jk, String tglLahir, String alamat, String noTelp) {
        this.idPasien = idPasien;
        this.namaPasien = namaPasien;
        this.jk = jk;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.noTelp = noTelp;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }

    public String getNamaPasien() {
        return namaPasien;
    }

    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }

    public String getJk() {
        return jk;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(String tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }
}
 