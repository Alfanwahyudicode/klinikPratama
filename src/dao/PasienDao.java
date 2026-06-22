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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author VanZ
 */
public class PasienDao {

// Ambil semua data pasien
    public List<Pasien> getAllPasien() {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien ORDER BY id_pasien ASC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Pasien p = new Pasien();
                p.setIdPasien(rs.getInt("id_pasien"));
                p.setNamaPasien(rs.getString("nama_pasien"));
                p.setTanggalLahir(rs.getString("tanggal_lahir"));
                p.setJenisKelamin(rs.getString("jenis_kelamin"));
                p.setAlamat(rs.getString("alamat"));
                p.setNoTelp(rs.getString("no_telp"));
                list.add(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pasien: " + e.getMessage());
        }
        return list;
    }
 
    // Cari pasien berdasarkan nama
    public List<Pasien> cariPasien(String keyword) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien WHERE nama_pasien LIKE ? ORDER BY id_pasien ASC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, "%" + keyword + "%");
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pasien p = new Pasien();
                    p.setIdPasien(rs.getInt("id_pasien"));
                    p.setNamaPasien(rs.getString("nama_pasien"));
                    p.setTanggalLahir(rs.getString("tanggal_lahir"));
                    p.setJenisKelamin(rs.getString("jenis_kelamin"));
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
 
    // Tambah pasien baru
    public boolean tambahPasien(Pasien p) {
        String sql = "INSERT INTO pasien (nama_pasien, tanggal_lahir, jenis_kelamin, alamat, no_telp) VALUES (?, ?, ?, ?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, p.getNamaPasien());
            ps.setString(2, p.getTanggalLahir());
            ps.setString(3, p.getJenisKelamin());
            ps.setString(4, p.getAlamat());
            ps.setString(5, p.getNoTelp());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah pasien: " + e.getMessage());
            return false;
        }
    }
 
    // Update data pasien
    public boolean updatePasien(Pasien p) {
        String sql = "UPDATE pasien SET nama_pasien=?, tanggal_lahir=?, jenis_kelamin=?, alamat=?, no_telp=? WHERE id_pasien=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, p.getNamaPasien());
            ps.setString(2, p.getTanggalLahir());
            ps.setString(3, p.getJenisKelamin());
            ps.setString(4, p.getAlamat());
            ps.setString(5, p.getNoTelp());
            ps.setInt(6, p.getIdPasien());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data pasien: " + e.getMessage());
            return false;
        }
    }
 
    // Hapus pasien
    public boolean hapusPasien(int idPasien) {
        String sql = "DELETE FROM pasien WHERE id_pasien=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idPasien);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus pasien: " + e.getMessage());
            return false;
        }
    }
}

    
    

    

