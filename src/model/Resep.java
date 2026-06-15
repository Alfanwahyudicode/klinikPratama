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
public class Resep {
     private int idResep;
    private int idPemeriksaan;
    private Date tanggalResep;

    // Konstruktor Default
    public Resep() {
    }

    // Konstruktor Custom
    public Resep(int idPemeriksaan, Date tanggalResep) {
        this.idPemeriksaan = idPemeriksaan;
        this.tanggalResep = tanggalResep;
    }

    // Getter dan Setter
    public int getIdResep() {
        return idResep;
    }

    public void setIdResep(int idResep) {
        this.idResep = idResep;
    }

    public int getIdPemeriksaan() {
        return idPemeriksaan;
    }

    public void setIdPemeriksaan(int idPemeriksaan) {
        this.idPemeriksaan = idPemeriksaan;
    }

    public Date getTanggalResep() {
        return tanggalResep;
    }

    public void setTanggalResep(Date tanggalResep) {
        this.tanggalResep = tanggalResep;
    }
    
}
