/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Date;
/**
 *
 * @author VanZ
 */

public class Kunjungan {
 
    private int idKunjungan;
    private int idPasien;
    private int idDokter;
    private Date tanggalKunjungan;
    private String keluhan;
    private String status; // daftar, selesai, batal
 
    // Field tambahan hasil JOIN, untuk ditampilkan di tabel (tidak disimpan ke DB)
    private String namaPasien;
    private String namaDokter;
 
    public Kunjungan() {
    }
 
    public Kunjungan(int idKunjungan, int idPasien, int idDokter, Date tanggalKunjungan, String keluhan, String status) {
        this.idKunjungan = idKunjungan;
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.tanggalKunjungan = tanggalKunjungan;
        this.keluhan = keluhan;
        this.status = status;
    }
 
    public int getIdKunjungan() {
        return idKunjungan;
    }
 
    public void setIdKunjungan(int idKunjungan) {
        this.idKunjungan = idKunjungan;
    }
 
    public int getIdPasien() {
        return idPasien;
    }
 
    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }
 
    public int getIdDokter() {
        return idDokter;
    }
 
    public void setIdDokter(int idDokter) {
        this.idDokter = idDokter;
    }
 
    public Date getTanggalKunjungan() {
        return tanggalKunjungan;
    }
 
    public void setTanggalKunjungan(Date tanggalKunjungan) {
        this.tanggalKunjungan = tanggalKunjungan;
    }
 
    public String getKeluhan() {
        return keluhan;
    }
 
    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public String getNamaPasien() {
        return namaPasien;
    }
 
    public void setNamaPasien(String namaPasien) {
        this.namaPasien = namaPasien;
    }
 
    public String getNamaDokter() {
        return namaDokter;
    }
 
    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }
}