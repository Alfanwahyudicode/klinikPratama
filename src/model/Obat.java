/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author VanZ
 */
public class Obat {
 
    public static final int BATAS_STOK_MENIPIS = 10;
 
    private int         idObat;
    private String      kodeObat;
    private String      namaObat;
    private String      jenis;
    private String      satuan;
    private int         stok;
    private BigDecimal  hargaBeli;
    private BigDecimal  hargaJual;
    private String      keterangan;
    private Timestamp   createdAt;

    public Obat() { }
 
    public Obat(String kodeObat, String namaObat, String jenis,
                String satuan, int stok,
                BigDecimal hargaBeli, BigDecimal hargaJual,
                String keterangan) {
        this.kodeObat   = kodeObat;
        this.namaObat   = namaObat;
        this.jenis      = jenis;
        this.satuan     = satuan;
        this.stok       = stok;
        this.hargaBeli  = hargaBeli;
        this.hargaJual  = hargaJual;
        this.keterangan = keterangan;
    }
 
    public Obat(int idObat, String kodeObat, String namaObat, String jenis,
                String satuan, int stok,
                BigDecimal hargaBeli, BigDecimal hargaJual,
                String keterangan, Timestamp createdAt) {
        this.idObat     = idObat;
        this.kodeObat   = kodeObat;
        this.namaObat   = namaObat;
        this.jenis      = jenis;
        this.satuan     = satuan;
        this.stok       = stok;
        this.hargaBeli  = hargaBeli;
        this.hargaJual  = hargaJual;
        this.keterangan = keterangan;
        this.createdAt  = createdAt;
    }
 
    public boolean isStokMenipis() {
        return stok < BATAS_STOK_MENIPIS;
    }
 
    public void kurangiStok(int jumlah) {
        if (jumlah > stok) {
            throw new IllegalArgumentException(
                "Stok tidak mencukupi. Stok tersedia: " + stok + ", diminta: " + jumlah);
        }
        this.stok -= jumlah;
    }
 

    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah tambah stok harus > 0.");
        }
        this.stok += jumlah;
    }
 

    public double getMarginPersen() {
        if (hargaBeli == null || hargaBeli.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return hargaJual.subtract(hargaBeli)
                        .divide(hargaBeli, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
    }
 
 
    public int getIdObat()              { return idObat; }
    public void setIdObat(int idObat)   { this.idObat = idObat; }
 
    public String getKodeObat()                     { return kodeObat; }
    public void setKodeObat(String kodeObat)        { this.kodeObat = kodeObat; }
 
    public String getNamaObat()                     { return namaObat; }
    public void setNamaObat(String namaObat)        { this.namaObat = namaObat; }
 
    public String getJenis()                        { return jenis; }
    public void setJenis(String jenis)              { this.jenis = jenis; }
 
    public String getSatuan()                       { return satuan; }
    public void setSatuan(String satuan)            { this.satuan = satuan; }
 
    public int getStok()                            { return stok; }
    public void setStok(int stok)                   { this.stok = stok; }
 
    public BigDecimal getHargaBeli()                { return hargaBeli; }
    public void setHargaBeli(BigDecimal hargaBeli)  { this.hargaBeli = hargaBeli; }
 
    public BigDecimal getHargaJual()                { return hargaJual; }
    public void setHargaJual(BigDecimal hargaJual)  { this.hargaJual = hargaJual; }
 
    public String getKeterangan()                   { return keterangan; }
    public void setKeterangan(String keterangan)    { this.keterangan = keterangan; }
 
    public Timestamp getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(Timestamp createdAt)   { this.createdAt = createdAt; }
 
    @Override
    public String toString() {
        return "[" + kodeObat + "] " + namaObat + " (" + satuan + ")";
    }
}