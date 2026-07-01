/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import koneksi.Koneksi;
import model.Resep;
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
public class ResepDao {

    /**
     * Nomor awal untuk ID Resep. Format tampilan yang diminta adalah
     * "28" + 4 digit urutan, mis. 280001, 280002, dst. Karena base-nya
     * 280000, maka nilai (base + urutan) sudah otomatis berformat seperti itu.
     */
    private static final int BASE_ID_RESEP = 280000;

    private void pastikanTabelSequenceAda(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS resep_id_sequence (id INT PRIMARY KEY, last_id INT NOT NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private int ambilIdTerbesarSaatIni(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id_resep) AS id_terbesar FROM resep WHERE id_resep BETWEEN "
                + BASE_ID_RESEP + " AND " + (BASE_ID_RESEP + 9999);
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int idTerbesar = rs.getInt("id_terbesar");
                if (!rs.wasNull() && idTerbesar >= BASE_ID_RESEP) {
                    return idTerbesar;
                }
            }
        }
        return BASE_ID_RESEP;
    }

    /**
     * Membuat ID Resep baru secara otomatis dengan format "28" + 4 digit
     * urutan (280001, 280002, dst). Nomor urut disimpan permanen pada tabel
     * resep_id_sequence sehingga SEKALI PAKAI: walaupun sebuah resep dengan
     * ID tertentu dihapus, nomor tersebut tidak akan pernah dipakai ulang
     * karena penghitung urutannya tidak pernah mundur.
     */
    public int generateIdResep() {
        try (Connection conn = Koneksi.getKoneksi()) {
            pastikanTabelSequenceAda(conn);

            int lastId;
            String cekSql = "SELECT last_id FROM resep_id_sequence WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(cekSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lastId = rs.getInt("last_id");
                } else {
                    lastId = ambilIdTerbesarSaatIni(conn);
                    String insertSql = "INSERT INTO resep_id_sequence (id, last_id) VALUES (1, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        psIns.setInt(1, lastId);
                        psIns.executeUpdate();
                    }
                }
            }

            int idBaru = lastId + 1;

            String updateSql = "UPDATE resep_id_sequence SET last_id = ? WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, idBaru);
                ps.executeUpdate();
            }

            return idBaru;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat ID Resep baru: " + e.getMessage());
            return BASE_ID_RESEP + 1;
        }
    }

    public int tambahResep(Resep r) {
        if (sudahAdaResep(r.getIdPemeriksaan())) {
            JOptionPane.showMessageDialog(null,
                "Pemeriksaan ini sudah memiliki resep!");
            return -1;
        }

        int idBaru = generateIdResep();
        String sql = "INSERT INTO resep (id_resep, id_pemeriksaan, tgl_resep) VALUES (?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBaru);
            ps.setInt(2, r.getIdPemeriksaan());
            ps.setDate(3, r.getTglResep());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                r.setIdResep(idBaru);
                return idBaru;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menambah resep: " + e.getMessage());
        }
        return -1;
    }
 
    public boolean updateResep(Resep r) {
        String sql = "UPDATE resep SET id_pemeriksaan=?, tgl_resep=? WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, r.getIdPemeriksaan());
            ps.setDate(2, r.getTglResep());
            ps.setInt(3, r.getIdResep());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengubah resep: " + e.getMessage());
            return false;
        }
    }
 
    public boolean hapusResep(int idResep) {
        new ResepDetailDao().deleteByResepId(idResep);
 
        String sql = "DELETE FROM resep WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghapus resep: " + e.getMessage());
            return false;
        }
    }
 
    public List<Resep> getAllResep() {
        List<Resep> list = new ArrayList<>();
        String sql = "SELECT * FROM resep ORDER BY id_resep DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil data resep: " + e.getMessage());
        }
        return list;
    }
 
    public Resep getById(int idResep) {
        Resep r = new Resep();
        String sql = "SELECT * FROM resep WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = mapRow(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil resep: " + e.getMessage());
        }
        return r;
    }
 
    public Resep getByIdPemeriksaan(int idPemeriksaan) {
        Resep r = null;
        String sql = "SELECT * FROM resep WHERE id_pemeriksaan=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idPemeriksaan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = mapRow(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil resep by pemeriksaan: " + e.getMessage());
        }
        return r;
    }
 
    public boolean sudahAdaResep(int idPemeriksaan) {
        String sql = "SELECT id_resep FROM resep WHERE id_pemeriksaan=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idPemeriksaan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal validasi resep: " + e.getMessage());
            return true;
        }
    }
 
    private Resep mapRow(ResultSet rs) throws SQLException {
        Resep r = new Resep();
        r.setIdResep(rs.getInt("id_resep"));
        r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
        r.setTglResep(rs.getDate("tgl_resep"));
        return r;
    }
}