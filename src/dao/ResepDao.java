/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import koneksi.Koneksi;
import model.Resep;
import model.ResepDetail;
 
import java.sql.Connection;
import java.sql.Date;
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
public class ResepDao {
 private final ResepDetailDao detailDao = new ResepDetailDao();
 
    // =========================================================
    // INSERT
    // =========================================================
 
    /**
     * Tambah resep baru.
     * Validasi: satu pemeriksaan hanya boleh punya satu resep.
     *
     * @return id_resep yang di-generate DB, atau -1 jika gagal
     */
    public int tambahResep(Resep r) {
        // Validasi: 1 pemeriksaan hanya 1 resep
        if (sudahAdaResep(r.getIdPemeriksaan())) {
            JOptionPane.showMessageDialog(null,
                "Pemeriksaan ini sudah memiliki resep!");
            return -1;
        }
 
        String sql = "INSERT INTO resep (id_pemeriksaan, tgl_resep) VALUES (?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(
                     sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
 
            ps.setInt(1, r.getIdPemeriksaan());
            ps.setDate(2, r.getTanggalResep() != null
                          ? r.getTanggalResep()
                          : new Date(System.currentTimeMillis()));
 
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idBaru = rs.getInt(1);
                        r.setIdResep(idBaru); // update objek agar caller bisa langsung pakai
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
 
    // =========================================================
    // UPDATE
    // =========================================================
 
    /**
     * Ubah tanggal resep.
     * (Data lain seperti id_pemeriksaan umumnya tidak diubah setelah dibuat.)
     */
    public boolean updateResep(Resep r) {
        String sql = "UPDATE resep SET tgl_resep=? WHERE id_resep=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setDate(1, r.getTanggalResep());
            ps.setInt(2, r.getIdResep());
 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengubah resep: " + e.getMessage());
            return false;
        }
    }
 
    // =========================================================
    // DELETE
    // =========================================================
 
    /**
     * Hapus resep beserta semua detail obatnya.
     * Urutan: hapus resep_detail dulu, baru hapus resep (referential integrity).
     */
    public boolean hapusResep(int idResep) {
        // hapus semua detail dulu
        detailDao.deleteByResepId(idResep);
 
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
 
    // =========================================================
    // SELECT – semua resep (untuk tabel di form utama)
    // =========================================================
 
    /**
     * Ambil semua resep, di-JOIN ke pemeriksaan, kunjungan, pasien, dan dokter.
     * Field bantu (namaPasien, namaDokter, noRm, diagnosa) langsung diisi.
     */
    public List<Resep> getAllResep() {
        List<Resep> list = new ArrayList<>();
 
        String sql = "SELECT r.id_resep, r.id_pemeriksaan, r.tgl_resep, "
                   + "       p.no_rm, p.nama_pasien, d.nama_dokter, pe.diagnosa "
                   + "FROM resep r "
                   + "JOIN pemeriksaan pe ON r.id_pemeriksaan = pe.id_pemeriksaan "
                   + "JOIN kunjungan k   ON pe.id_kunjungan   = k.id_kunjungan "
                   + "JOIN pasien p      ON k.id_pasien        = p.id_pasien "
                   + "JOIN dokter d      ON k.id_dokter         = d.id_dokter "
                   + "ORDER BY r.id_resep DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(mapRowResep(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil data resep: " + e.getMessage());
        }
        return list;
    }
 
    // =========================================================
    // SELECT – cari resep berdasarkan keyword
    // =========================================================
 
    /**
     * Cari resep berdasarkan nama pasien atau nomor RM.
     */
    public List<Resep> cariResep(String keyword) {
        List<Resep> list = new ArrayList<>();
 
        String sql = "SELECT r.id_resep, r.id_pemeriksaan, r.tgl_resep, "
                   + "       p.no_rm, p.nama_pasien, d.nama_dokter, pe.diagnosa "
                   + "FROM resep r "
                   + "JOIN pemeriksaan pe ON r.id_pemeriksaan = pe.id_pemeriksaan "
                   + "JOIN kunjungan k   ON pe.id_kunjungan   = k.id_kunjungan "
                   + "JOIN pasien p      ON k.id_pasien        = p.id_pasien "
                   + "JOIN dokter d      ON k.id_dokter         = d.id_dokter "
                   + "WHERE p.nama_pasien LIKE ? OR p.no_rm LIKE ? "
                   + "ORDER BY r.id_resep DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowResep(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mencari resep: " + e.getMessage());
        }
        return list;
    }
 
    // =========================================================
    // SELECT – satu resep lengkap dengan list detail-nya
    // =========================================================
 
    /**
     * Ambil satu resep berdasarkan id_resep,
     * termasuk semua ResepDetail yang dimilikinya.
     *
     * @param idResep id resep yang dicari
     * @return objek Resep lengkap, atau null jika tidak ditemukan
     */
    public Resep getByIdResep(int idResep) {
        String sql = "SELECT r.id_resep, r.id_pemeriksaan, r.tgl_resep, "
                   + "       p.no_rm, p.nama_pasien, d.nama_dokter, pe.diagnosa "
                   + "FROM resep r "
                   + "JOIN pemeriksaan pe ON r.id_pemeriksaan = pe.id_pemeriksaan "
                   + "JOIN kunjungan k   ON pe.id_kunjungan   = k.id_kunjungan "
                   + "JOIN pasien p      ON k.id_pasien        = p.id_pasien "
                   + "JOIN dokter d      ON k.id_dokter         = d.id_dokter "
                   + "WHERE r.id_resep = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idResep);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resep r = mapRowResep(rs);
                    // langsung load detail-nya sekali juga
                    r.setListDetail(detailDao.getByResepId(idResep));
                    return r;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil resep: " + e.getMessage());
        }
        return null;
    }
 
    // =========================================================
    // SELECT – resep milik satu pemeriksaan
    // =========================================================
 
    /**
     * Ambil resep berdasarkan id_pemeriksaan.
     * Dipakai untuk mengecek dan menampilkan resep di FormResep
     * ketika user memilih pemeriksaan dari combobox.
     *
     * @return objek Resep, atau null jika pemeriksaan belum punya resep
     */
    public Resep getByPemeriksaanId(int idPemeriksaan) {
        String sql = "SELECT r.id_resep, r.id_pemeriksaan, r.tgl_resep, "
                   + "       p.no_rm, p.nama_pasien, d.nama_dokter, pe.diagnosa "
                   + "FROM resep r "
                   + "JOIN pemeriksaan pe ON r.id_pemeriksaan = pe.id_pemeriksaan "
                   + "JOIN kunjungan k   ON pe.id_kunjungan   = k.id_kunjungan "
                   + "JOIN pasien p      ON k.id_pasien        = p.id_pasien "
                   + "JOIN dokter d      ON k.id_dokter         = d.id_dokter "
                   + "WHERE r.id_pemeriksaan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idPemeriksaan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resep r = mapRowResep(rs);
                    r.setListDetail(detailDao.getByResepId(r.getIdResep()));
                    return r;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil resep by pemeriksaan: " + e.getMessage());
        }
        return null;
    }
 
    // =========================================================
    // Validasi – cek apakah pemeriksaan sudah punya resep
    // =========================================================
 
    /**
     * Validasi: satu pemeriksaan hanya boleh memiliki satu resep.
     *
     * @return true jika pemeriksaan sudah memiliki resep
     */
    public boolean sudahAdaResep(int idPemeriksaan) {
        String sql = "SELECT id_resep FROM resep WHERE id_pemeriksaan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idPemeriksaan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // ada baris berarti sudah ada resep
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal validasi resep: " + e.getMessage());
            return true; // anggap sudah ada agar tidak duplikat saat error
        }
    }
 
    /**
     * Ambil daftar pemeriksaan yang BELUM mempunyai resep.
     * Digunakan untuk mengisi ComboBox di FormResep agar user
     * hanya bisa membuat resep dari pemeriksaan yang memang belum ada resepnya.
     *
     * Field yang dikembalikan: id_pemeriksaan, no_rm, nama_pasien, diagnosa
     * (disimpan sebagai objek Resep tanpa id_resep / tgl_resep karena belum ada).
     */
    public List<Resep> getPemeriksaanBelumResep() {
        List<Resep> list = new ArrayList<>();
 
        String sql = "SELECT pe.id_pemeriksaan, pe.diagnosa, "
                   + "       p.no_rm, p.nama_pasien, d.nama_dokter "
                   + "FROM pemeriksaan pe "
                   + "JOIN kunjungan k ON pe.id_kunjungan = k.id_kunjungan "
                   + "JOIN pasien p    ON k.id_pasien      = p.id_pasien "
                   + "JOIN dokter d    ON k.id_dokter       = d.id_dokter "
                   + "WHERE pe.id_pemeriksaan NOT IN "
                   + "      (SELECT id_pemeriksaan FROM resep) "
                   + "ORDER BY pe.id_pemeriksaan DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Resep r = new Resep();
                r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
                r.setDiagnosa(rs.getString("diagnosa"));
                r.setNoRm(rs.getString("no_rm"));
                r.setNamaPasien(rs.getString("nama_pasien"));
                r.setNamaDokter(rs.getString("nama_dokter"));
                list.add(r);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil daftar pemeriksaan: " + e.getMessage());
        }
        return list;
    }
 
    // =========================================================
    // Helper – mapping ResultSet → Resep (tanpa list detail)
    // =========================================================
 
    private Resep mapRowResep(ResultSet rs) throws SQLException {
        Resep r = new Resep();
        r.setIdResep(rs.getInt("id_resep"));
        r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
        r.setTanggalResep(rs.getDate("tgl_resep"));
        r.setNoRm(rs.getString("no_rm"));
        r.setNamaPasien(rs.getString("nama_pasien"));
        r.setNamaDokter(rs.getString("nama_dokter"));
        r.setDiagnosa(rs.getString("diagnosa"));
        return r;
    }
}
