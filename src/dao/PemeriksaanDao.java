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

    // 1. Mengambil data kunjungan yang MEMILIKI STATUS 'Daftar' dan Belum Diperiksa
    public List<Object[]> getKunjunganBelumPeriksa() {
        List<Object[]> list = new ArrayList<>();
        // Murni mengambil kolom yang ada tanpa memaksakan no_rm
        String sql = "SELECT k.id_kunjungan, pa.nama_pasien, d.nama_dokter, k.keluhan "
                + "FROM kunjungan k "
                + "JOIN pasien pa ON k.id_pasien = pa.id_pasien "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "WHERE k.status = 'Daftar' "
                + "AND k.id_kunjungan NOT IN (SELECT id_kunjungan FROM pemeriksaan)";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_kunjungan"), // Index 0
                    rs.getString("nama_pasien"), // Index 1
                    rs.getString("nama_dokter"), // Index 2
                    rs.getString("keluhan") // Index 3
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
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    // 4. Tambah Pemeriksaan SEKALIGUS Mengubah Status Kunjungan Menjadi 'Selesai'
    public boolean tambahPemeriksaan(Pemeriksaan pe) {
        String sqlPemeriksaan = "INSERT INTO pemeriksaan (id_kunjungan, diagnosa, tindakan, catatan, biaya_tindakan) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateKunjungan = "UPDATE kunjungan SET status = 'Selesai' WHERE id_kunjungan = ?";

        Connection conn = null;
        try {
            conn = Koneksi.getKoneksi();
            conn.setAutoCommit(false); // Mengaktifkan Transaction agar kedua query sinkron

            // Query A: Input data pemeriksaan
            try (PreparedStatement psPemeriksaan = conn.prepareStatement(sqlPemeriksaan, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psPemeriksaan.setInt(1, pe.getIdKunjungan());
                psPemeriksaan.setString(2, pe.getDiagnosa());
                psPemeriksaan.setString(3, pe.getTindakan());
                psPemeriksaan.setString(4, pe.getCatatan());
                psPemeriksaan.setBigDecimal(5, pe.getBiayaTindakan());

                int rows = psPemeriksaan.executeUpdate();
                if (rows > 0) {
                    try (ResultSet generatedKeys = psPemeriksaan.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            pe.setIdPemeriksaan(generatedKeys.getInt(1));
                        }
                    }

                    // Query B: Update status di tabel kunjungan menjadi 'Selesai'
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateKunjungan)) {
                        psUpdate.setInt(1, pe.getIdKunjungan());
                        psUpdate.executeUpdate();
                    }

                    conn.commit(); // Eksekusi sukses semua
                    return true;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // 5. Mengambil Objek Pemeriksaan tunggal berdasarkan ID
    public Pemeriksaan getPemeriksaanById(int id) {
        String sql = "SELECT * FROM pemeriksaan WHERE id_pemeriksaan = ?";
        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    // 6. Hapus Pemeriksaan SEKALIGUS Mengembalikan Status Kunjungan ke 'Daftar'
    public boolean hapusPemeriksaan(int idPemeriksaan) {
        String sqlGetKunjungan = "SELECT id_kunjungan FROM pemeriksaan WHERE id_pemeriksaan = ?";
        String sqlDelete = "DELETE FROM pemeriksaan WHERE id_pemeriksaan = ?";
        String sqlRestoreStatus = "UPDATE kunjungan SET status = 'Daftar' WHERE id_kunjungan = ?";

        Connection conn = null;
        try {
            conn = Koneksi.getKoneksi();
            conn.setAutoCommit(false); // Mengaktifkan Transaction

            int idKunjungan = 0;
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetKunjungan)) {
                psGet.setInt(1, idPemeriksaan);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        idKunjungan = rs.getInt("id_kunjungan");
                    }
                }
            }

            if (idKunjungan != 0) {
                // Query A: Hapus data pemeriksaan
                try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                    psDelete.setInt(1, idPemeriksaan);
                    psDelete.executeUpdate();
                }

                // Query B: Kembalikan status kunjungan menjadi 'Daftar'
                try (PreparedStatement psRestore = conn.prepareStatement(sqlRestoreStatus)) {
                    psRestore.setInt(1, idKunjungan);
                    psRestore.executeUpdate();
                }

                conn.commit(); // Eksekusi sukses semua
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // 7. Mengambil Semua Data Pemeriksaan Berbentuk Objek List
    public List<Pemeriksaan> getAllPemeriksaan() {
        List<Pemeriksaan> list = new ArrayList<>();
        String sql = "SELECT p.id_pemeriksaan, p.id_kunjungan, p.diagnosa, p.tindakan, p.catatan, p.biaya_tindakan "
                + "FROM pemeriksaan p "
                + "ORDER BY p.id_pemeriksaan DESC";

        try (Connection conn = Koneksi.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pemeriksaan pe = new Pemeriksaan();
                pe.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
                pe.setIdKunjungan(rs.getInt("id_kunjungan"));
                pe.setDiagnosa(rs.getString("diagnosa"));
                pe.setTindakan(rs.getString("tindakan"));
                pe.setCatatan(rs.getString("catatan"));
                pe.setBiayaTindakan(rs.getBigDecimal("biaya_tindakan"));

                list.add(pe);
            }
        } catch (SQLException e) {
            System.out.println("Error getAllPemeriksaan: " + e.getMessage());
        }
        return list;
    }
}
