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
        String sql = "SELECT * FROM obat ORDER BY id_obat ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data obat: " + e.getMessage());
        }
        return list;
    }

    // Cari obat berdasarkan nama / kode
    public List<Obat> cariObat(String keyword) {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat WHERE nama_obat LIKE ? OR kode_obat LIKE ? ORDER BY id_obat ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

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

    // Ambil daftar obat dengan stok menipis (stok < BATAS_STOK_MENIPIS)
    public List<Obat> getObatStokMenipis() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat WHERE stok < ? ORDER BY stok ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Obat.BATAS_STOK_MENIPIS);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data stok menipis: " + e.getMessage());
        }
        return list;
    }

    // Tambah obat baru
    public boolean tambahObat(Obat o) {
        String sql = "INSERT INTO obat (kode_obat, nama_obat, jenis, satuan, stok, harga_beli, harga_jual, keterangan) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getKodeObat());
            ps.setString(2, o.getNamaObat());
            ps.setString(3, o.getJenis());
            ps.setString(4, o.getSatuan());
            ps.setInt(5, o.getStok());
            ps.setBigDecimal(6, o.getHargaBeli());
            ps.setBigDecimal(7, o.getHargaJual());
            ps.setString(8, o.getKeterangan());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah obat: " + e.getMessage());
            return false;
        }
    }

    // Update data obat
    public boolean updateObat(Obat o) {
        String sql = "UPDATE obat SET kode_obat=?, nama_obat=?, jenis=?, satuan=?, stok=?, harga_beli=?, harga_jual=?, keterangan=? "
                   + "WHERE id_obat=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getKodeObat());
            ps.setString(2, o.getNamaObat());
            ps.setString(3, o.getJenis());
            ps.setString(4, o.getSatuan());
            ps.setInt(5, o.getStok());
            ps.setBigDecimal(6, o.getHargaBeli());
            ps.setBigDecimal(7, o.getHargaJual());
            ps.setString(8, o.getKeterangan());
            ps.setInt(9, o.getIdObat());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data obat: " + e.getMessage());
            return false;
        }
    }

    // Update stok saja (dipakai saat resep/pemakaian obat)
    public boolean updateStok(int idObat, int stokBaru) {
        String sql = "UPDATE obat SET stok=? WHERE id_obat=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idObat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus obat: " + e.getMessage());
            return false;
        }
    }

    // Helper: mapping ResultSet -> Obat
    private Obat mapRow(ResultSet rs) throws SQLException {
        Obat o = new Obat();
        o.setIdObat(rs.getInt("id_obat"));
        o.setKodeObat(rs.getString("kode_obat"));
        o.setNamaObat(rs.getString("nama_obat"));
        o.setJenis(rs.getString("jenis"));
        o.setSatuan(rs.getString("satuan"));
        o.setStok(rs.getInt("stok"));
        o.setHargaBeli(rs.getBigDecimal("harga_beli"));
        o.setHargaJual(rs.getBigDecimal("harga_jual"));
        o.setKeterangan(rs.getString("keterangan"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}