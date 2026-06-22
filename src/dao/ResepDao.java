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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author VanZ
 */
public class ResepDao {
 
    public int tambahResep(Resep r) {
        if (sudahAdaResep(r.getIdPemeriksaan())) {
            JOptionPane.showMessageDialog(null,
                "Pemeriksaan ini sudah memiliki resep!");
            return -1;
        }
 
        String sql = "INSERT INTO resep (id_pemeriksaan, tgl_resep) VALUES (?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
 
            ps.setInt(1, r.getIdPemeriksaan());
            ps.setDate(2, r.getTglResep());
 
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idBaru = rs.getInt(1);
                        r.setIdResep(idBaru);
                        return idBaru;
                    }
                }
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