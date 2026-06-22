package dao;

import koneksi.Koneksi;
import model.Pemeriksaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Alfan Wahyudi
 */
public class PemeriksaanDao {

    public List<Pemeriksaan> getAllPemeriksaan() {
        List<Pemeriksaan> list = new ArrayList<>();
        String sql = "SELECT * FROM pemeriksaan ORDER BY id_pemeriksaan ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pemeriksaan: " + e.getMessage());
        }
        return list;
    }

    public Pemeriksaan getPemeriksaanById(int idPemeriksaan) {
        String sql = "SELECT * FROM pemeriksaan WHERE id_pemeriksaan = ?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPemeriksaan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pemeriksaan: " + e.getMessage());
        }
        return null;
    }

    public List<Object[]> getDataTabel() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pe.id_pemeriksaan, p.no_rm, p.nama_pasien, d.nama_dokter, "
                   + "pe.diagnosa, pe.tindakan, pe.biaya_tindakan "
                   + "FROM pemeriksaan pe "
                   + "JOIN kunjungan k ON pe.id_kunjungan = k.id_kunjungan "
                   + "JOIN pasien p ON k.id_pasien = p.id_pasien "
                   + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                   + "ORDER BY pe.id_pemeriksaan ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_pemeriksaan"),
                    rs.getString("no_rm"),
                    rs.getString("nama_pasien"),
                    rs.getString("nama_dokter"),
                    rs.getString("diagnosa"),
                    rs.getString("tindakan"),
                    rs.getBigDecimal("biaya_tindakan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pemeriksaan: " + e.getMessage());
        }
        return list;
    }

    public List<Object[]> cariDataTabel(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT pe.id_pemeriksaan, p.no_rm, p.nama_pasien, d.nama_dokter, "
                   + "pe.diagnosa, pe.tindakan, pe.biaya_tindakan "
                   + "FROM pemeriksaan pe "
                   + "JOIN kunjungan k ON pe.id_kunjungan = k.id_kunjungan "
                   + "JOIN pasien p ON k.id_pasien = p.id_pasien "
                   + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                   + "WHERE p.nama_pasien LIKE ? OR p.no_rm LIKE ? "
                   + "ORDER BY pe.id_pemeriksaan ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_pemeriksaan"),
                        rs.getString("no_rm"),
                        rs.getString("nama_pasien"),
                        rs.getString("nama_dokter"),
                        rs.getString("diagnosa"),
                        rs.getString("tindakan"),
                        rs.getBigDecimal("biaya_tindakan")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mencari data pemeriksaan: " + e.getMessage());
        }
        return list;
    }

    public List<Object[]> getKunjunganBelumPeriksa() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT k.id_kunjungan, p.no_rm, p.nama_pasien, d.nama_dokter, k.keluhan "
                   + "FROM kunjungan k "
                   + "JOIN pasien p ON k.id_pasien = p.id_pasien "
                   + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                   + "WHERE k.status = 'daftar' "
                   + "AND k.id_kunjungan NOT IN (SELECT id_kunjungan FROM pemeriksaan) "
                   + "ORDER BY k.tgl_kunjungan ASC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_kunjungan"),
                    rs.getString("no_rm"),
                    rs.getString("nama_pasien"),
                    rs.getString("nama_dokter"),
                    rs.getString("keluhan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data kunjungan: " + e.getMessage());
        }
        return list;
    }

    public boolean sudahDiperiksa(int idKunjungan) {
        String sql = "SELECT id_pemeriksaan FROM pemeriksaan WHERE id_kunjungan = ?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idKunjungan);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal validasi pemeriksaan: " + e.getMessage());
            return true;
        }
    }

    public boolean tambahPemeriksaan(Pemeriksaan pe) {
        if (sudahDiperiksa(pe.getIdKunjungan())) {
            JOptionPane.showMessageDialog(null, "Kunjungan ini sudah memiliki data pemeriksaan!");
            return false;
        }

        String sql = "INSERT INTO pemeriksaan (id_kunjungan, diagnosa, tindakan, catatan, biaya_tindakan) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pe.getIdKunjungan());
            ps.setString(2, pe.getDiagnosa());
            ps.setString(3, pe.getTindakan());
            ps.setString(4, pe.getCatatan());
            ps.setBigDecimal(5, pe.getBiayaTindakan());

            boolean berhasil = ps.executeUpdate() > 0;

            if (berhasil) {
                updateStatusKunjunganSelesai(conn, pe.getIdKunjungan());
            }

            return berhasil;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambah pemeriksaan: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePemeriksaan(Pemeriksaan pe) {
        String sql = "UPDATE pemeriksaan SET diagnosa=?, tindakan=?, catatan=?, biaya_tindakan=? "
                   + "WHERE id_pemeriksaan=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pe.getDiagnosa());
            ps.setString(2, pe.getTindakan());
            ps.setString(3, pe.getCatatan());
            ps.setBigDecimal(4, pe.getBiayaTindakan());
            ps.setInt(5, pe.getIdPemeriksaan());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data pemeriksaan: " + e.getMessage());
            return false;
        }
    }

    public boolean hapusPemeriksaan(int idPemeriksaan) {
        String sql = "DELETE FROM pemeriksaan WHERE id_pemeriksaan=?";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPemeriksaan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus pemeriksaan: " + e.getMessage());
            return false;
        }
    }

    private void updateStatusKunjunganSelesai(Connection conn, int idKunjungan) throws SQLException {
        String sql = "UPDATE kunjungan SET status='selesai' WHERE id_kunjungan=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKunjungan);
            ps.executeUpdate();
        }
    }

    private Pemeriksaan mapRow(ResultSet rs) throws SQLException {
        Pemeriksaan pe = new Pemeriksaan();
        pe.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
        pe.setIdKunjungan(rs.getInt("id_kunjungan"));
        pe.setDiagnosa(rs.getString("diagnosa"));
        pe.setTindakan(rs.getString("tindakan"));
        pe.setCatatan(rs.getString("catatan"));
        pe.setBiayaTindakan(rs.getBigDecimal("biaya_tindakan"));
        return pe;
    }
}