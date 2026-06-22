/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author VanZ
 */
public class ResepDetail {
    private int        idResepDetail;
    private int        idResep;
    private int        idObat;
    private int        jumlah;
    private String     aturanPakai;
    private BigDecimal hargaSatuan;
    private BigDecimal subtotal;
 
    public ResepDetail() {}
 
    public ResepDetail(int idResep, int idObat, int jumlah,
                       String aturanPakai, BigDecimal hargaSatuan, BigDecimal subtotal) {
        this.idResep     = idResep;
        this.idObat      = idObat;
        this.jumlah      = jumlah;
        this.aturanPakai = aturanPakai;
        this.hargaSatuan = hargaSatuan;
        this.subtotal    = subtotal;
    }
 
    public int getIdResepDetail()                    { return idResepDetail; }
    public void setIdResepDetail(int idResepDetail)  { this.idResepDetail = idResepDetail; }
 
    public int getIdResep()                  { return idResep; }
    public void setIdResep(int idResep)      { this.idResep = idResep; }
 
    public int getIdObat()               { return idObat; }
    public void setIdObat(int idObat)    { this.idObat = idObat; }
 
    public int getJumlah()               { return jumlah; }
    public void setJumlah(int jumlah)    { this.jumlah = jumlah; }
 
    public String getAturanPakai()                   { return aturanPakai; }
    public void setAturanPakai(String aturanPakai)   { this.aturanPakai = aturanPakai; }
 
    public BigDecimal getHargaSatuan()                   { return hargaSatuan; }
    public void setHargaSatuan(BigDecimal hargaSatuan)   { this.hargaSatuan = hargaSatuan; }
 
    public BigDecimal getSubtotal()                  { return subtotal; }
    public void setSubtotal(BigDecimal subtotal)     { this.subtotal = subtotal; }
 
    @Override
    public String toString() {
        return "ResepDetail #" + idResepDetail + " | Obat #" + idObat
                + " | Jumlah: " + jumlah + " | Subtotal: " + subtotal;
    }
    
}
