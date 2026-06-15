/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import koneksi.Koneksi;
import model.ResepDetail;
 
import java.math.BigDecimal;
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
public class ResepDetailDao {
public int tambahDetail(ResepDetail rd) {
        
        rd.hitungSubtotal();
 
        String sql = "INSERT INTO resep_detail "
                   + "(id_resep, id_obat, jumlah, aturan_pakai, harga_satuan, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(
                     sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
 
            ps.setInt(1, rd.getIdResep());
            ps.setInt(2, rd.getIdObat());
            ps.setInt(3, rd.getJumlah());
            ps.setString(4, rd.getAturanPakai());
            ps.setBigDecimal(5, rd.getHargaSatuan());
            ps.setBigDecimal(6, rd.getSubtotal());
 
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menambah detail resep: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateDetail(ResepDetail rd) {
        rd.hitungSubtotal(); 
 
        String sql = "UPDATE resep_detail SET "
                   + "id_obat=?, jumlah=?, aturan_pakai=?, harga_satuan=?, subtotal=? "
                   + "WHERE id_resep_detail=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, rd.getIdObat());
            ps.setInt(2, rd.getJumlah());
            ps.setString(3, rd.getAturanPakai());
            ps.setBigDecimal(4, rd.getHargaSatuan());
            ps.setBigDecimal(5, rd.getSubtotal());
            ps.setInt(6, rd.getIdResepDetail());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengubah detail resep: " + e.getMessage());
            return false;
        }
    }

    public boolean hapusDetail(int idResepDetail) {
        String sql = "DELETE FROM resep_detail WHERE id_resep_detail=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResepDetail);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghapus detail resep: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteByResepId(int idResep) {
        String sql = "DELETE FROM resep_detail WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
            ps.executeUpdate(); 
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghapus detail resep: " + e.getMessage());
            return false;
        }
    }
 
    public List<ResepDetail> getByResepId(int idResep) {
        List<ResepDetail> list = new ArrayList<>();
 
        String sql = "SELECT rd.id_resep_detail, rd.id_resep, rd.id_obat, "
                   + "       rd.jumlah, rd.aturan_pakai, rd.harga_satuan, rd.subtotal, "
                   + "       o.nama_obat, o.satuan "
                   + "FROM resep_detail rd "
                   + "LEFT JOIN obat o ON rd.id_obat = o.id_obat "
                   + "WHERE rd.id_resep = ? "
                   + "ORDER BY rd.id_resep_detail ASC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil detail resep: " + e.getMessage());
        }
        return list;
    }
 
    public BigDecimal getTotalByResepId(int idResep) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(subtotal) AS total FROM resep_detail WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("total") != null) {
                    total = rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghitung total detail resep: " + e.getMessage());
        }
        return total;
    }

    private ResepDetail mapRow(ResultSet rs) throws SQLException {
        ResepDetail rd = new ResepDetail();
        rd.setIdResepDetail(rs.getInt("id_resep_detail"));
        rd.setIdResep(rs.getInt("id_resep"));
        rd.setIdObat(rs.getInt("id_obat"));
        rd.setJumlah(rs.getInt("jumlah"));
        rd.setAturanPakai(rs.getString("aturan_pakai"));
        rd.setHargaSatuan(rs.getBigDecimal("harga_satuan"));
        rd.setSubtotal(rs.getBigDecimal("subtotal"));
        rd.setNamaObat(rs.getString("nama_obat"));
        rd.setSatuan(rs.getString("satuan"));
        return rd;
    }
}
