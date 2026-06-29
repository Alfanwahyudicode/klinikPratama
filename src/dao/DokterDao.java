package dao;

import koneksi.Koneksi; // Sesuaikan dengan package koneksi di proyek Anda
import model.Dokter;   // Sesuaikan dengan package model di proyek Anda
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Data Access Object (DAO) untuk pengelolaan data Master Dokter Berdasarkan
 * database Klinik Pratama
 *
 * @author VanZ
 */
public class DokterDao {

    public boolean tambahDokter(Dokter d) {
        // Query SQL menggunakan 3 kolom sesuai rancangan database tabel dokter
        String sql = "INSERT INTO dokter (nama_dokter, spesialisasi, no_telp) VALUES (?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getNamaDokter());
            ps.setString(2, d.getSpesialisasi());
            ps.setString(3, d.getNoTelp());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah data dokter: " + e.getMessage());
            return false;
        }
    }

    public List<Dokter> getAllDokter() {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM dokter";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dokter d = new Dokter();
                d.setIdDokter(rs.getInt("id_dokter"));
                d.setNamaDokter(rs.getString("nama_dokter"));
                d.setSpesialisasi(rs.getString("spesialisasi"));
                d.setNoTelp(rs.getString("no_telp"));
                list.add(d);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data dokter: " + e.getMessage());
        }
        return list;
    }

    public boolean updateDokter(Dokter d) {
       
        String sql = "UPDATE dokter SET nama_dokter = ?, spesialisasi = ?, no_telp = ? WHERE id_dokter = ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNamaDokter());   
            ps.setString(2, d.getSpesialisasi());  
            ps.setString(3, d.getNoTelp());        
            ps.setInt(4, d.getIdDokter());         

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data dokter: " + e.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean hapusDokter(int id) {
        // Pastikan query SQL memiliki klausa WHERE dan tanda tanya (?)
        String sql = "DELETE FROM dokter WHERE id_dokter = ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // Mengisi tanda tanya ke-1 dengan data ID

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Data Dokter tidak dapat dihapus karena masih digunakan dalam riwayat kunjungan pasien!",
                        "Peringatan Keamanan Data", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Dokter> cariDokter(String keyword) {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM dokter WHERE nama_dokter LIKE ? OR spesialisasi LIKE ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword + "%";
            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dokter d = new Dokter();
                    d.setIdDokter(rs.getInt("id_dokter"));
                    d.setNamaDokter(rs.getString("nama_dokter"));
                    d.setSpesialisasi(rs.getString("spesialisasi"));
                    d.setNoTelp(rs.getString("no_telp"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data dokter: " + e.getMessage());
        }
        return list;
    }
}
