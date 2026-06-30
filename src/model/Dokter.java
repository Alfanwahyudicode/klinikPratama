package model;

/**
 * Model class untuk mencerminkan data tabel dokter dari database.
 *
 * @author VanZ
 */
public class Dokter {

    private int idDokter;
    private String namaDokter;
    private String spesialisasi;
    private String noTelp;

    // Constructor Kosong
    public Dokter() {
    }

    // Constructor dengan Parameter
    public Dokter(int idDokter, String namaDokter, String spesialisasi, String noTelp) {
        this.idDokter = idDokter;
        this.namaDokter = namaDokter;
        this.spesialisasi = spesialisasi;
        this.noTelp = noTelp;
    }

    // Getter dan Setter
    public int getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(int idDokter) {
        this.idDokter = idDokter;
    }

    public String getNamaDokter() {
        return namaDokter;
    }

    public void setNamaDokter(String namaDokter) {
        this.namaDokter = namaDokter;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public void setNoTelp(long noTelp) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
