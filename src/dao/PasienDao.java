/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
 
import koneksi.Koneksi;
import model.Pasien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
 
/**
 *
 * @author VanZ
 */
public class PasienDao {
 
    private static final int PREFIX_ID = 150000; // 15 + 0000
    private static final String TABEL_COUNTER = "pasien_id_counter";

    private void pastikanTabelCounterAda(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABEL_COUNTER + " ("
                    + "id INT PRIMARY KEY, "
                    + "last_no INT NOT NULL"
                    + ")");
            st.executeUpdate("INSERT IGNORE INTO " + TABEL_COUNTER + " (id, last_no) VALUES (1, 0)");
        }
    }
 
    /**
     * @return ID pasien baru (contoh: 150001), atau -1 jika gagal.
     */
    public synchronized int generateIdPasien() {
        Connection conn = null;
        try {
            conn = Koneksi.getKoneksi();
            if (conn == null) {
                return -1;
            }
            conn.setAutoCommit(false);
            pastikanTabelCounterAda(conn);
 
            int lastNo = 0;
            try (PreparedStatement lock = conn.prepareStatement(
                    "SELECT last_no FROM " + TABEL_COUNTER + " WHERE id = 1 FOR UPDATE");
                 ResultSet rs = lock.executeQuery()) {
                if (rs.next()) {
                    lastNo = rs.getInt("last_no");
                }
            }
 
            try (PreparedStatement cek = conn.prepareStatement(
                    "SELECT COALESCE(MAX(id_pasien), 0) AS maxId FROM pasien");
                 ResultSet rs = cek.executeQuery()) {
                if (rs.next()) {
                    int selisih = rs.getInt("maxId") - PREFIX_ID;
                    if (selisih > lastNo) {
                        lastNo = selisih;
                    }
                }
            }
 
            int nextNo = lastNo + 1;
            try (PreparedStatement up = conn.prepareStatement(
                    "UPDATE " + TABEL_COUNTER + " SET last_no = ? WHERE id = 1")) {
                up.setInt(1, nextNo);
                up.executeUpdate();
            }
 
            conn.commit();
            return PREFIX_ID + nextNo;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            JOptionPane.showMessageDialog(null, "Gagal membuat ID Pasien baru: " + e.getMessage());
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
 
    public boolean tambahPasien(Pasien p) {
        String sql = "INSERT INTO pasien (id_pasien, nama_pasien, jk, tgl_lahir, alamat, no_telp) VALUES (?, ?, ?, ?, ?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, p.getIdPasien());
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
                p.setNamaPasien(rs.getString("nama_pasien"));
                p.setJk(rs.getString("jk"));
                p.setTglLahir(rs.getString("tgl_lahir"));
                p.setAlamat(rs.getString("alamat"));
                p.setNoTelp(rs.getString("no_telp"));
                list.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pasien: " + e.getMessage());
        }
        return list;
    }
 
    public Pasien getPasienById(int id) {
        String sql = "SELECT * FROM pasien WHERE id_pasien = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pasien p = new Pasien();
                    p.setIdPasien(rs.getInt("id_pasien"));
                    p.setNamaPasien(rs.getString("nama_pasien"));
                    p.setJk(rs.getString("jk"));
                    p.setTglLahir(rs.getString("tgl_lahir"));
                    p.setAlamat(rs.getString("alamat"));
                    p.setNoTelp(rs.getString("no_telp"));
                    return p;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pasien: " + e.getMessage());
        }
        return null;
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
        String sql = "SELECT * FROM pasien WHERE CAST(id_pasien AS CHAR) LIKE ? OR nama_pasien LIKE ? ORDER BY id_pasien DESC";
 
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
 
            String value = "%" + keyword + "%";
            ps.setString(1, value);
            ps.setString(2, value);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pasien p = new Pasien();
                    p.setIdPasien(rs.getInt("id_pasien"));
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