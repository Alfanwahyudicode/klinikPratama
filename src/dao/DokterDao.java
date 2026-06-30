package dao;

import koneksi.Koneksi;
import model.Dokter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Data Access Object (DAO) untuk pengolahan query database tabel dokter.
 *
 * @author VanZ
 */
public class DokterDao {

    // 1. TAMBAH DATA DOKTER (CREATE)
    public boolean tambahDokter(Dokter d) {
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

    // 2. TAMPILKAN SEMUA DATA DOKTER (READ)
    public List<Dokter> getAllDokter() {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM dokter ORDER BY id_dokter DESC";

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

    // 3. UBAH DATA DOKTER (UPDATE) - Memiliki 4 parameter tanda tanya (?)
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
            JOptionPane.showMessageDialog(null, "Gagal mengubah data dokter: " + e.getMessage());
            return false;
        }
    }

    // 4. HAPUS DATA DOKTER (DELETE) - Disertai handling Foreign Key Terikat
    public boolean hapusDokter(int id) {
        String sql = "DELETE FROM dokter WHERE id_dokter = ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            // Error code 1451 adalah MySQL constraint foreign key terikat tabel lain
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Gagal Menghapus! Data dokter tidak dapat dihapus karena masih digunakan di data kunjungan pasien.",
                        "Peringatan Database", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data dokter: " + e.getMessage());
            }
            return false;
        }
    }

    // 5. CARI DATA DOKTER (SEARCH)
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
