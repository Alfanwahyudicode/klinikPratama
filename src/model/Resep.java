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
      private int  idResep;
    private int  idPemeriksaan;
    private Date tglResep;
 
    public Resep() {}
 
    public Resep(int idPemeriksaan, Date tglResep) {
        this.idPemeriksaan = idPemeriksaan;
        this.tglResep      = tglResep;
    }
 
    public int getIdResep()                  { return idResep; }
    public void setIdResep(int idResep)      { this.idResep = idResep; }
 
    public int getIdPemeriksaan()                    { return idPemeriksaan; }
    public void setIdPemeriksaan(int idPemeriksaan)  { this.idPemeriksaan = idPemeriksaan; }
 
    public Date getTglResep()                { return tglResep; }
    public void setTglResep(Date tglResep)   { this.tglResep = tglResep; }
 
    @Override
    public String toString() {
        return "Resep #" + idResep + " | Pemeriksaan #" + idPemeriksaan + " | " + tglResep;
    }

}
