/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import koneksi.Koneksi;
import model.Obat;

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
public class ObatDao {

    // Ambil semua data obat
    public List<Obat> getAllObat() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat ORDER BY id_id_obat ASC"; // Sesuai query asalmu atau "id_obat"

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data obat: " + e.getMessage());
        }
        return list;
    }

    // Cari obat berdasarkan nama
    public List<Obat> cariObat(String keyword) {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat WHERE nama_obat LIKE ? ORDER BY id_obat ASC";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data obat: " + e.getMessage());
        }
        return list;
    }

    // Tambah obat baru (Sesuai 100% dengan kolom databasemu)
    public boolean tambahObat(Obat o) {
        String sql = "INSERT INTO obat (nama_obat, satuan, stok, harga_jual) VALUES (?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getNamaObat());
            ps.setString(2, o.getSatuan());
            ps.setInt(3, o.getStok());
            ps.setBigDecimal(4, o.getHargaJual());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah obat: " + e.getMessage());
            return false;
        }
    }

    // Update data obat lengkap berdasarkan ID Obat
    public boolean updateObat(Obat o) {
        String sql = "UPDATE obat SET nama_obat=?, satuan=?, stok=?, harga_jual=? WHERE id_obat=?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getNamaObat());
            ps.setString(2, o.getSatuan());
            ps.setInt(3, o.getStok());
            ps.setBigDecimal(4, o.getHargaJual());
            ps.setInt(5, o.getIdObat());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data obat: " + e.getMessage());
            return false;
        }
    }

    // Update stok saja (dipakai saat resep/pemakaian obat)
    public boolean updateStok(int idObat, int stokBaru) {
        String sql = "UPDATE obat SET stok=? WHERE id_obat=?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stokBaru);
            ps.setInt(2, idObat);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal update stok obat: " + e.getMessage());
            return false;
        }
    }

    // Hapus obat
    public boolean hapusObat(int idObat) {
        String sql = "DELETE FROM obat WHERE id_obat=?";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idObat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus obat: " + e.getMessage());
            return false;
        }
    }

    // Helper: mapping ResultSet -> Obat (Sesuai database asli kamu)
    private Obat mapRow(ResultSet rs) throws SQLException {
        Obat o = new Obat();
        o.setIdObat(rs.getInt("id_obat"));
        o.setNamaObat(rs.getString("nama_obat"));
        o.setSatuan(rs.getString("satuan"));
        o.setStok(rs.getInt("stok"));
        o.setHargaJual(rs.getBigDecimal("harga_jual"));
        return o;
    }
}
