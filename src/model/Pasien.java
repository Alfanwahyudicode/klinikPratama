package model;

/**
 * Model class Pasien - Sesuai 100% dengan struktur tabel database.
 *
 * @author VanZ
 */
public class Pasien {

    private int idPasien;
    private String noRm;
    private String namaPasien;
    private String jk; // Menggunakan 'jk' untuk mencerminkan enum('L','P')
    private String tglLahir; // Sementara String format (YYYY-MM-DD) atau bisa dikosongkan jika belum ada di GUI
    private String alamat;
    private String noTelp; // Sesuai kolom database: no_telp

    public Pasien() {
    }

    public Pasien(int idPasien, String noRm, String namaPasien, String jk, String tglLahir, String alamat, String noTelp) {
        this.idPasien = idPasien;
        this.noRm = noRm;
        this.namaPasien = namaPasien;
        this.jk = jk;
        this.tglLahir = tglLahir;
        this.alamat = alamat;
        this.noTelp = noTelp;
    }

    // Getter dan Setter
    public int getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }

    public String getNoRm() {
        return noRm;
    }

    public void setNoRm(String noRm) {
        this.noRm = noRm;
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
