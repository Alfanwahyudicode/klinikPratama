package dao;

import koneksi.Koneksi;
import model.Pasien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author VanZ
 */
public class PasienDao {

    public boolean tambahPasien(Pasien p) {
        String sql = "INSERT INTO pasien (no_rm, nama_pasien, jk, tgl_lahir, alamat, no_telp) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNoRm() == null ? "-" : p.getNoRm());
            ps.setString(2, p.getNamaPasien());
            ps.setString(3, p.getJk());
            ps.setString(4, p.getTglLahir() == null ? "2000-01-01" : p.getTglLahir());
            ps.setString(5, p.getAlamat());
            ps.setString(6, p.getNoTelp());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah data pasien: " + e.getMessage());
            return false;
        }
    }

    public List<Pasien> getAllPasien() {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien ORDER BY id_pasien DESC";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pasien p = new Pasien();
                p.setIdPasien(rs.getInt("id_pasien"));
                p.setNoRm(rs.getString("no_rm"));
                p.setNamaPasien(rs.getString("nama_pasien"));
                p.setJk(rs.getString("jk"));
                p.setTglLahir(rs.getString("tgl_lahir"));
                p.setAlamat(rs.getString("alamat"));
                p.setNoTelp(rs.getString("no_telp"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pasien: " + e.getMessage());
        }
        return list;
    }

    public boolean updatePasien(Pasien p) {
        String sql = "UPDATE pasien SET nama_pasien = ?, jk = ?, tgl_lahir = ?, alamat = ?, no_telp = ? WHERE id_pasien = ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaPasien());
            ps.setString(2, p.getJk());
            ps.setString(3, p.getTglLahir() == null ? "2000-01-01" : p.getTglLahir());
            ps.setString(4, p.getAlamat());
            ps.setString(5, p.getNoTelp());
            ps.setInt(6, p.getIdPasien());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data pasien: " + e.getMessage());
            return false;
        }
    }

    public boolean hapusPasien(int id) {
        String sql = "DELETE FROM pasien WHERE id_pasien = ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(null,
                        "Data pasien gagal dihapus karena terelasi dengan tabel transaksi medis lain.",
                        "Peringatan Constraint", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data pasien: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Pasien> cariPasien(String keyword) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien WHERE nama_pasien LIKE ? OR alamat LIKE ?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword + "%";
            ps.setString(1, value);
            ps.setString(2, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pasien p = new Pasien();
                    p.setIdPasien(rs.getInt("id_pasien"));
                    p.setNoRm(rs.getString("no_rm"));
                    p.setNamaPasien(rs.getString("nama_pasien"));
                    p.setJk(rs.getString("jk"));
                    p.setTglLahir(rs.getString("tgl_lahir"));
                    p.setAlamat(rs.getString("alamat"));
                    p.setNoTelp(rs.getString("no_telp"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data pasien: " + e.getMessage());
        }
        return list;
    }
}