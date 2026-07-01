/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import koneksi.Koneksi;
import model.Kunjungan;
 
import java.sql.Connection;
import java.sql.Date;
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
public class KunjunganDao {
 
    private static final String BASE_SELECT =
            "SELECT k.id_kunjungan, k.id_pasien, k.id_dokter, k.tanggal_kunjungan, "
            + "k.keluhan, k.status, p.nama_pasien, d.nama_dokter "
            + "FROM kunjungan k "
            + "JOIN pasien p ON k.id_pasien = p.id_pasien "
            + "JOIN dokter d ON k.id_dokter = d.id_dokter ";
 
    private Kunjungan mapRow(ResultSet rs) throws SQLException {
        Kunjungan k = new Kunjungan();
        k.setIdKunjungan(rs.getInt("id_kunjungan"));
        k.setIdPasien(rs.getInt("id_pasien"));
        k.setIdDokter(rs.getInt("id_dokter"));
        k.setTanggalKunjungan(rs.getDate("tanggal_kunjungan"));
        k.setKeluhan(rs.getString("keluhan"));
        k.setStatus(rs.getString("status"));
        k.setNamaPasien(rs.getString("nama_pasien"));
        k.setNamaDokter(rs.getString("nama_dokter"));
        return k;
    }
 
    public List<Kunjungan> getAllKunjungan() {
        List<Kunjungan> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY k.id_kunjungan DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data kunjungan: " + e.getMessage());
        }
        return list;
    }
    public int generateAutoId() {
        int nextId = 240001; // ID Awal jika database kosong
        String sql = "SELECT MAX(id_kunjungan) FROM kunjungan";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (maxId > 0) {
                    nextId = maxId + 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextId;
    }
 
    public Kunjungan getKunjunganById(int idKunjungan) {
        String sql = BASE_SELECT + "WHERE k.id_kunjungan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idKunjungan);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data kunjungan: " + e.getMessage());
        }
        return null;
    }
 
    public List<Kunjungan> filterKunjungan(Date tanggal, Integer idDokter, String status) {
        List<Kunjungan> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
 
        if (tanggal != null) {
            sql.append("AND k.tanggal_kunjungan = ? ");
        }
        if (idDokter != null) {
            sql.append("AND k.id_dokter = ? ");
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND k.status = ? ");
        }
        sql.append("ORDER BY k.id_kunjungan DESC");
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
 
            int idx = 1;
            if (tanggal != null) {
                ps.setDate(idx++, tanggal);
            }
            if (idDokter != null) {
                ps.setInt(idx++, idDokter);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(idx++, status);
            }
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memfilter data kunjungan: " + e.getMessage());
        }
        return list;
    }
 
    public List<Kunjungan> cariKunjunganByNamaPasien(String keyword) {
        List<Kunjungan> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.nama_pasien LIKE ? ORDER BY k.id_kunjungan DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, "%" + keyword + "%");
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data kunjungan: " + e.getMessage());
        }
        return list;
    }
 
    public boolean tambahKunjungan(Kunjungan k) {
        String sql = "INSERT INTO kunjungan (id_pasien, id_dokter, tanggal_kunjungan, keluhan, status) "
                + "VALUES (?, ?, ?, ?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, k.getIdPasien());
            ps.setInt(2, k.getIdDokter());
            ps.setDate(3, k.getTanggalKunjungan());
            ps.setString(4, k.getKeluhan());
            ps.setString(5, k.getStatus() != null ? k.getStatus() : "daftar");
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah kunjungan: " + e.getMessage());
            return false;
        }
    }
 
    public boolean updateKunjungan(Kunjungan k) {
        String sql = "UPDATE kunjungan SET id_pasien=?, id_dokter=?, tanggal_kunjungan=?, keluhan=?, status=? "
                + "WHERE id_kunjungan=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, k.getIdPasien());
            ps.setInt(2, k.getIdDokter());
            ps.setDate(3, k.getTanggalKunjungan());
            ps.setString(4, k.getKeluhan());
            ps.setString(5, k.getStatus());
            ps.setInt(6, k.getIdKunjungan());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data kunjungan: " + e.getMessage());
            return false;
        }
    }
 
    public boolean updateStatusKunjungan(int idKunjungan, String status) {
        String sql = "UPDATE kunjungan SET status=? WHERE id_kunjungan=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, status);
            ps.setInt(2, idKunjungan);
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah status kunjungan: " + e.getMessage());
            return false;
        }
    }
 
    public boolean hapusKunjungan(int idKunjungan) {
        String sql = "DELETE FROM kunjungan WHERE id_kunjungan=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idKunjungan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus kunjungan: " + e.getMessage());
            return false;
        }
    }
    
    public int getNextIdKunjungan() {
        String sql = "SELECT AUTO_INCREMENT FROM information_schema.TABLES "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'kunjungan'";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int next = rs.getInt(1);
                return next > 0 ? next : 1;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil ID kunjungan berikutnya: " + e.getMessage());
        }
        return 1;
    }

        public List<Kunjungan> cariKunjungan(String keyword) {
        List<Kunjungan> list = new ArrayList<>();
        String sql = BASE_SELECT
                + "WHERE p.nama_pasien LIKE ? OR k.id_kunjungan = ? OR k.id_pasien = ? "
                + "ORDER BY k.id_kunjungan DESC";

        String angkaSaja = keyword == null ? "" : keyword.replaceAll("[^0-9]", "");
        int idCocok = -1;
        if (!angkaSaja.isEmpty()) {
            try {
                idCocok = Integer.parseInt(angkaSaja);
            } catch (NumberFormatException ignored) {
                idCocok = -1;
            }
        }

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, idCocok);
            ps.setInt(3, idCocok);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data kunjungan: " + e.getMessage());
        }
        return list;
    }
}