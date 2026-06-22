/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author VanZ
 */
public class DokterDao {

    // Ambil semua data dokter
    public List<Dokter> getAllDokter() {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM dokter ORDER BY id_dokter ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dokter d = new Dokter();
                d.setIdDokter(rs.getInt("id_dokter"));
                d.setKodeDokter(rs.getString("kode_dokter"));
                d.setNamaDokter(rs.getString("nama_dokter"));
                d.setSpesialisasi(rs.getString("spesialis"));
                d.setNoTelp(rs.getString("no_telp"));
                list.add(d);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data dokter: " + e.getMessage());
        }
        return list;
    }

    // Cari dokter berdasarkan nama
    public List<Dokter> cariDokter(String keyword) {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT * FROM dokter WHERE nama_dokter LIKE ? ORDER BY id_dokter ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dokter d = new Dokter();
                    d.setIdDokter(rs.getInt("id_dokter"));
                    d.setKodeDokter(rs.getString("kode_dokter"));
                    d.setNamaDokter(rs.getString("nama_dokter"));
                    d.setSpesialisasi(rs.getString("spesialis"));
                    d.setNoTelp(rs.getString("no_telp"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data dokter: " + e.getMessage());
        }
        return list;
    }

    // Tambah dokter baru
    public boolean tambahDokter(Dokter d) {
        String sql = "INSERT INTO dokter (kode_dokter, nama_dokter, spesialis, no_telp) VALUES (?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getKodeDokter());
            ps.setString(2, d.getNamaDokter());
            ps.setString(3, d.getSpesialisasi());
            ps.setString(4, d.getNoTelp());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah dokter: " + e.getMessage());
            return false;
        }
    }

    // Update data dokter
    public boolean updateDokter(Dokter d) {
        String sql = "UPDATE dokter SET kode_dokter=?, nama_dokter=?, spesialis=?, no_telp=? WHERE id_dokter=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getKodeDokter());
            ps.setString(2, d.getNamaDokter());
            ps.setString(3, d.getSpesialisasi());
            ps.setString(4, d.getNoTelp());
            ps.setInt(5, d.getIdDokter());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data dokter: " + e.getMessage());
            return false;
        }
    }

    // Hapus dokter
    public boolean hapusDokter(int idDokter) {
        String sql = "DELETE FROM dokter WHERE id_dokter=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDokter);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus dokter: " + e.getMessage());
            return false;
        }
    }
}

