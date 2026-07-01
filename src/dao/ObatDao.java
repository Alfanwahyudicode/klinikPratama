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

    public List<Obat> getAllObat() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat ORDER BY id_obat ASC";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data obat: " + e.getMessage());
        }
        return list;
    }

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

    private void pastikanTabelSequenceAda(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS obat_id_sequence (id INT PRIMARY KEY, last_id INT NOT NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private int ambilIdTerbesarSaatIni(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id_obat) AS id_terbesar FROM obat WHERE id_obat BETWEEN 130000 AND 139999";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int idTerbesar = rs.getInt("id_terbesar");
                if (!rs.wasNull() && idTerbesar >= 130000) {
                    return idTerbesar;
                }
            }
        }
        return 130000;
    }

    public int generateIdObat() {
        try (Connection conn = Koneksi.getKoneksi()) {
            pastikanTabelSequenceAda(conn);

            int lastId;
            String cekSql = "SELECT last_id FROM obat_id_sequence WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(cekSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lastId = rs.getInt("last_id");
                } else {
                    lastId = ambilIdTerbesarSaatIni(conn);
                    String insertSql = "INSERT INTO obat_id_sequence (id, last_id) VALUES (1, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        psIns.setInt(1, lastId);
                        psIns.executeUpdate();
                    }
                }
            }

            int idBaru = lastId + 1;

            String updateSql = "UPDATE obat_id_sequence SET last_id = ? WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, idBaru);
                ps.executeUpdate();
            }

            return idBaru;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat ID Obat baru: " + e.getMessage());
            return 130001;
        }
    }

    public boolean tambahObat(Obat o) {
        String sql = "INSERT INTO obat (id_obat, nama_obat, satuan, stok, harga_jual) VALUES (?, ?, ?, ?, ?)";

        o.setIdObat(generateIdObat());

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, o.getIdObat());
            ps.setString(2, o.getNamaObat());
            ps.setString(3, o.getSatuan());
            ps.setInt(4, o.getStok());
            ps.setBigDecimal(5, o.getHargaJual());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah obat: " + e.getMessage());
            return false;
        }
    }

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