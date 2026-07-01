/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
 
import java.math.BigDecimal;
 
public class Pembayaran {
 
   private int idBayar;
    private int idKunjungan;
    private BigDecimal totalTindakan;
    private BigDecimal totalObat;
    private BigDecimal totalBayar;
    private String tglBayar;
    private String metodeBayar;
    private String kodePembayaran;
 
    public Pembayaran() {
    }
 
    public Pembayaran(int idKunjungan, BigDecimal totalTindakan,
            BigDecimal totalObat, BigDecimal totalBayar,
            String tglBayar) {
        this.idKunjungan = idKunjungan;
        this.totalTindakan = totalTindakan;
        this.totalObat = totalObat;
        this.totalBayar = totalBayar;
        this.tglBayar = tglBayar;
    }
 
    public int getIdBayar() {
        return idBayar;
    }
 
    public void setIdBayar(int idBayar) {
        this.idBayar = idBayar;
    }
 
    public int getIdKunjungan() {
        return idKunjungan;
    }
 
    public void setIdKunjungan(int idKunjungan) {
        this.idKunjungan = idKunjungan;
    }
 
    public BigDecimal getTotalTindakan() {
        return totalTindakan;
    }
 
    public void setTotalTindakan(BigDecimal totalTindakan) {
        this.totalTindakan = totalTindakan;
    }
 
    public BigDecimal getTotalObat() {
        return totalObat;
    }
 
    public void setTotalObat(BigDecimal totalObat) {
        this.totalObat = totalObat;
    }
 
    public BigDecimal getTotalBayar() {
        return totalBayar;
    }
 
    public void setTotalBayar(BigDecimal totalBayar) {
        this.totalBayar = totalBayar;
    }
 
    public String getTglBayar() {
        return tglBayar;
    }
 
    public void setTglBayar(String tglBayar) {
        this.tglBayar = tglBayar;
    }
 
    public String getMetodeBayar() {
        return metodeBayar;
    }
 
    public void setMetodeBayar(String metodeBayar) {
        this.metodeBayar = metodeBayar;
    }
 
    public String getKodePembayaran() {
        return kodePembayaran;
    }
 
    public void setKodePembayaran(String kodePembayaran) {
        this.kodePembayaran = kodePembayaran;
    }
 
    @Override
    public String toString() {
        return "Pembayaran #" + idBayar + " - Kunjungan: " + idKunjungan;
    }
}