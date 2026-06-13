/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author Alfan Wahyudi
 */
public class Pemeriksaan {

    private int idPemeriksaan;
    private int idKunjungan;
    private String diagnosa;
    private String tindakan;
    private String catatan;
    private BigDecimal biayaTindakan;

    // ── Tambahan untuk tampilan (hasil JOIN, tidak disimpan ke tabel pemeriksaan) ──
    private String noRm;
    private String namaPasien;
    private String namaDokter;
    private String tglKunjungan;
    private String keluhan;

    public Pemeriksaan() {}

    public Pemeriksaan(int idPemeriksaan, int idKunjungan, String diagnosa, String tindakan,
                        String catatan, BigDecimal biayaTindakan) {
        this.idPemeriksaan = idPemeriksaan;
        this.idKunjungan = idKunjungan;
        this.diagnosa = diagnosa;
        this.tindakan = tindakan;
        this.catatan = catatan;
        this.biayaTindakan = biayaTindakan;
    }

    public int getIdPemeriksaan() { return idPemeriksaan; }
    public void setIdPemeriksaan(int idPemeriksaan) { this.idPemeriksaan = idPemeriksaan; }

    public int getIdKunjungan() { return idKunjungan; }
    public void setIdKunjungan(int idKunjungan) { this.idKunjungan = idKunjungan; }

    public String getDiagnosa() { return diagnosa; }
    public void setDiagnosa(String diagnosa) { this.diagnosa = diagnosa; }

    public String getTindakan() { return tindakan; }
    public void setTindakan(String tindakan) { this.tindakan = tindakan; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public BigDecimal getBiayaTindakan() { return biayaTindakan; }
    public void setBiayaTindakan(BigDecimal biayaTindakan) { this.biayaTindakan = biayaTindakan; }

    public String getNoRm() { return noRm; }
    public void setNoRm(String noRm) { this.noRm = noRm; }

    public String getNamaPasien() { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }

    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }

    public String getTglKunjungan() { return tglKunjungan; }
    public void setTglKunjungan(String tglKunjungan) { this.tglKunjungan = tglKunjungan; }

    public String getKeluhan() { return keluhan; }
    public void setKeluhan(String keluhan) { this.keluhan = keluhan; }
}
