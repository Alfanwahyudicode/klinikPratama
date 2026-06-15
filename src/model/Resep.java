/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author VanZ
 */
public class Resep {
       private int    idResep;
    private int    idPemeriksaan;
    private Date   tanggalResep;
 
    private String namaPasien;
    private String namaDokter;
    private String noRm;
    private String diagnosa;
 
    private List<ResepDetail> listDetail = new ArrayList<>();
  
    public Resep() {}
 
    public Resep(int idPemeriksaan, Date tanggalResep) {
        this.idPemeriksaan = idPemeriksaan;
        this.tanggalResep  = tanggalResep;
    }

    public BigDecimal getTotalObat() {
        BigDecimal total = BigDecimal.ZERO;
        for (ResepDetail rd : listDetail) {
            if (rd.getSubtotal() != null) {
                total = total.add(rd.getSubtotal());
            }
        }
        return total;
    }
 
    public int getIdResep()                  { return idResep; }
    public void setIdResep(int idResep)      { this.idResep = idResep; }
 
    public int getIdPemeriksaan()                    { return idPemeriksaan; }
    public void setIdPemeriksaan(int idPemeriksaan)  { this.idPemeriksaan = idPemeriksaan; }
 
    public Date getTanggalResep()                { return tanggalResep; }
    public void setTanggalResep(Date tanggalResep) { this.tanggalResep = tanggalResep; }
 
    public String getNamaPasien()                { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }
 
    public String getNamaDokter()                { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
 
    public String getNoRm()              { return noRm; }
    public void setNoRm(String noRm)     { this.noRm = noRm; }
 
    public String getDiagnosa()                  { return diagnosa; }
    public void setDiagnosa(String diagnosa)     { this.diagnosa = diagnosa; }
 
    public List<ResepDetail> getListDetail()                         { return listDetail; }
    public void setListDetail(List<ResepDetail> listDetail)          { this.listDetail = listDetail; }
 
    @Override
    public String toString() {
        return "Resep #" + idResep + " | Pemeriksaan #" + idPemeriksaan
               + " | " + (namaPasien != null ? namaPasien : "") 
               + " | " + tanggalResep;
    }

}
