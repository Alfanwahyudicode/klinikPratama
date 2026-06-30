package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Pemeriksaan;
import koneksi.Koneksi; // Memastikan import package koneksi Anda sudah benar

public class PemeriksaanDao {

    // 1. Mengambil data kunjungan yang belum diperiksa
    public List<Object[]> getKunjunganBelumPeriksa() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT k.id_kunjungan, k.no_rm, pa.nama_pasien, d.nama_dokter, k.keluhan "
                + "FROM kunjungan k "
                + "JOIN pasien pa ON k.id_pasien = pa.id_pasien "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "WHERE k.id_kunjungan NOT IN (SELECT id_kunjungan FROM pemeriksaan)";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
            e.printStackTrace();
        }
        return list;
    }

    // 2. Mengambil data untuk JTable (URUT: id, pasien, dokter, diagnosa, tindakan, catatan, biaya)
    public List<Object[]> getDataTabel() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.id_pemeriksaan, pa.nama_pasien, d.nama_dokter, p.diagnosa, p.tindakan, p.catatan, p.biaya_tindakan "
                + "FROM pemeriksaan p "
                + "JOIN kunjungan k ON p.id_kunjungan = k.id_kunjungan "
                + "JOIN pasien pa ON k.id_pasien = pa.id_pasien "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "ORDER BY p.id_pemeriksaan DESC";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_pemeriksaan"),
                    rs.getString("nama_pasien"),
                    rs.getString("nama_dokter"),
                    rs.getString("diagnosa"),
                    rs.getString("tindakan"),
                    rs.getString("catatan"),
                    rs.getBigDecimal("biaya_tindakan")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Fungsi Pencarian (Urutan SELECT disamakan dengan getDataTabel)
    public List<Object[]> cariDataTabel(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.id_pemeriksaan, pa.nama_pasien, d.nama_dokter, p.diagnosa, p.tindakan, p.catatan, p.biaya_tindakan "
                + "FROM pemeriksaan p "
                + "JOIN kunjungan k ON p.id_kunjungan = k.id_kunjungan "
                + "JOIN pasien pa ON k.id_pasien = pa.id_pasien "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "WHERE pa.nama_pasien LIKE ? OR d.nama_dokter LIKE ? OR p.diagnosa LIKE ?";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_pemeriksaan"),
                        rs.getString("nama_pasien"),
                        rs.getString("nama_dokter"),
                        rs.getString("diagnosa"),
                        rs.getString("tindakan"),
                        rs.getString("catatan"),
                        rs.getBigDecimal("biaya_tindakan")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. Tambah Pemeriksaan
    public boolean tambahPemeriksaan(Pemeriksaan pe) {
        String sql = "INSERT INTO pemeriksaan (id_kunjungan, diagnosa, tindakan, catatan, biaya_tindakan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pe.getIdKunjungan());
            ps.setString(2, pe.getDiagnosa());
            ps.setString(3, pe.getTindakan());
            ps.setString(4, pe.getCatatan());
            ps.setBigDecimal(5, pe.getBiayaTindakan());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pe.setIdPemeriksaan(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Mengambil Objek Pemeriksaan tunggal berdasarkan ID
    public Pemeriksaan getPemeriksaanById(int id) {
        String sql = "SELECT * FROM pemeriksaan WHERE id_pemeriksaan = ?";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 6. Hapus Pemeriksaan
    public boolean hapusPemeriksaan(int id) {
        String sql = "DELETE FROM pemeriksaan WHERE id_pemeriksaan = ?";
        try (Connection conn = Koneksi.getKoneksi(); // DIUBAH MENJADI getKoneksi()
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
