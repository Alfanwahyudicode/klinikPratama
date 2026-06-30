package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author VanZ
 */
public class Obat {

    public static final int BATAS_STOK_MENIPIS = 10;

    // 5 Atribut yang sesuai 100% dengan isi database/ObatDao kamu
    private int idObat;
    private String namaObat;
    private String satuan;
    private int stok;
    private BigDecimal hargaJual;

    // Konstruktor Kosong
    public Obat() {
    }

    // Konstruktor Sesuai Parameter Asli
    public Obat(String kodeObat, String namaObat, String jenis,
            String satuan, int stok,
            BigDecimal hargaBeli, BigDecimal hargaJual,
            String keterangan) {
        this.namaObat = namaObat;
        this.satuan = satuan;
        this.stok = stok;
        this.hargaJual = hargaJual;
    }

    // Konstruktor dengan ID dan Timestamp
    public Obat(int idObat, String kodeObat, String namaObat, String jenis,
            String satuan, int stok,
            BigDecimal hargaBeli, BigDecimal hargaJual,
            String keterangan, Timestamp createdAt) {
        this.idObat = idObat;
        this.namaObat = namaObat;
        this.satuan = satuan;
        this.stok = stok;
        this.hargaJual = hargaJual;
    }

    // Logika Bisnis Aturan Stok
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

    // ==========================================
    // GETTER DAN SETTER (Sesuai 100% dengan ObatDao)
    // ==========================================
    public int getIdObat() {
        return idObat;
    }

    public void setIdObat(int idObat) {
        this.idObat = idObat;
    }

    public String getNamaObat() {
        return namaObat;
    }

    public void setNamaObat(String namaObat) {
        this.namaObat = namaObat;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        this.hargaJual = hargaJual;
    }
}
