/*
 * LaporanDao.java
 * DAO khusus untuk kebutuhan modul Laporan.
 * CATATAN PENTING: DAO ini TIDAK membuat tabel baru sama sekali.
 * Semua query di bawah hanya melakukan SELECT (join & agregasi) terhadap
 * tabel-tabel yang sudah ada: pasien, dokter, kunjungan, pemeriksaan,
 * resep, resep_detail, obat, dan pembayaran.
 */
package dao;

import koneksi.Koneksi;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author VanZ
 */
public class LaporanDao {

    // =====================================================================
    // 1. LAPORAN KUNJUNGAN PER PERIODE
    // Kolom hasil: id_kunjungan, tanggal_kunjungan, nama_pasien, nama_dokter,
    //              keluhan, status
    // =====================================================================
    public List<Object[]> getKunjunganPerPeriode(Date tglAwal, Date tglAkhir, Integer idDokter, String status) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT k.id_kunjungan, k.tanggal_kunjungan, p.nama_pasien, d.nama_dokter, "
                + "k.keluhan, k.status "
                + "FROM kunjungan k "
                + "JOIN pasien p ON k.id_pasien = p.id_pasien "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "WHERE 1=1 ");

        if (tglAwal != null) {
            sql.append("AND k.tanggal_kunjungan >= ? ");
        }
        if (tglAkhir != null) {
            sql.append("AND k.tanggal_kunjungan <= ? ");
        }
        if (idDokter != null) {
            sql.append("AND k.id_dokter = ? ");
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND k.status = ? ");
        }
        sql.append("ORDER BY k.tanggal_kunjungan ASC, k.id_kunjungan ASC");

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tglAwal != null) {
                ps.setDate(idx++, tglAwal);
            }
            if (tglAkhir != null) {
                ps.setDate(idx++, tglAkhir);
            }
            if (idDokter != null) {
                ps.setInt(idx++, idDokter);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(idx++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_kunjungan"),
                        rs.getDate("tanggal_kunjungan"),
                        rs.getString("nama_pasien"),
                        rs.getString("nama_dokter"),
                        rs.getString("keluhan"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil laporan kunjungan: " + e.getMessage());
        }
        return list;
    }

    // =====================================================================
    // 2. LAPORAN PENDAPATAN PER PERIODE (berdasarkan tabel pembayaran)
    // Kolom hasil: id_bayar, tgl_bayar, nama_pasien, metode_bayar,
    //              total_tindakan, total_obat, total_bayar
    // =====================================================================
    public List<Object[]> getPendapatanPerPeriode(String tglAwal, String tglAkhir) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.id_bayar, b.tgl_bayar, p.nama_pasien, b.metode_bayar, "
                + "b.total_tindakan, b.total_obat, b.total_bayar "
                + "FROM pembayaran b "
                + "JOIN kunjungan k ON b.id_kunjungan = k.id_kunjungan "
                + "JOIN pasien p ON k.id_pasien = p.id_pasien "
                + "WHERE 1=1 ");

        if (tglAwal != null && !tglAwal.isEmpty()) {
            sql.append("AND b.tgl_bayar >= ? ");
        }
        if (tglAkhir != null && !tglAkhir.isEmpty()) {
            sql.append("AND b.tgl_bayar <= ? ");
        }
        sql.append("ORDER BY b.tgl_bayar ASC, b.id_bayar ASC");

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tglAwal != null && !tglAwal.isEmpty()) {
                ps.setString(idx++, tglAwal);
            }
            if (tglAkhir != null && !tglAkhir.isEmpty()) {
                ps.setString(idx++, tglAkhir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_bayar"),
                        rs.getString("tgl_bayar"),
                        rs.getString("nama_pasien"),
                        rs.getString("metode_bayar"),
                        rs.getBigDecimal("total_tindakan"),
                        rs.getBigDecimal("total_obat"),
                        rs.getBigDecimal("total_bayar")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil laporan pendapatan: " + e.getMessage());
        }
        return list;
    }

    /**
     * @return array BigDecimal berisi {totalTindakan, totalObat, totalBayar}
     * untuk periode yang sama dengan getPendapatanPerPeriode.
     */
    public BigDecimal[] getTotalPendapatan(String tglAwal, String tglAkhir) {
        BigDecimal[] hasil = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(total_tindakan),0) AS s_tindakan, "
                + "COALESCE(SUM(total_obat),0) AS s_obat, "
                + "COALESCE(SUM(total_bayar),0) AS s_bayar "
                + "FROM pembayaran WHERE 1=1 ");

        if (tglAwal != null && !tglAwal.isEmpty()) {
            sql.append("AND tgl_bayar >= ? ");
        }
        if (tglAkhir != null && !tglAkhir.isEmpty()) {
            sql.append("AND tgl_bayar <= ? ");
        }

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tglAwal != null && !tglAwal.isEmpty()) {
                ps.setString(idx++, tglAwal);
            }
            if (tglAkhir != null && !tglAkhir.isEmpty()) {
                ps.setString(idx++, tglAkhir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasil[0] = rs.getBigDecimal("s_tindakan");
                    hasil[1] = rs.getBigDecimal("s_obat");
                    hasil[2] = rs.getBigDecimal("s_bayar");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghitung total pendapatan: " + e.getMessage());
        }
        return hasil;
    }

    // =====================================================================
    // 3. LAPORAN OBAT TERPAKAI (berdasarkan resep_detail), per periode
    //    tanggal kunjungan.
    // Kolom hasil: id_obat, nama_obat, satuan, total_jumlah, total_nilai
    // =====================================================================
    public List<Object[]> getObatTerpakai(Date tglAwal, Date tglAkhir) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT o.id_obat, o.nama_obat, o.satuan, "
                + "SUM(rd.jumlah) AS total_jumlah, "
                + "SUM(rd.subtotal) AS total_nilai "
                + "FROM resep_detail rd "
                + "JOIN obat o ON rd.id_obat = o.id_obat "
                + "JOIN resep r ON rd.id_resep = r.id_resep "
                + "JOIN pemeriksaan pm ON r.id_pemeriksaan = pm.id_pemeriksaan "
                + "JOIN kunjungan k ON pm.id_kunjungan = k.id_kunjungan "
                + "WHERE 1=1 ");

        if (tglAwal != null) {
            sql.append("AND k.tanggal_kunjungan >= ? ");
        }
        if (tglAkhir != null) {
            sql.append("AND k.tanggal_kunjungan <= ? ");
        }
        sql.append("GROUP BY o.id_obat, o.nama_obat, o.satuan ORDER BY total_jumlah DESC");

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tglAwal != null) {
                ps.setDate(idx++, tglAwal);
            }
            if (tglAkhir != null) {
                ps.setDate(idx++, tglAkhir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_obat"),
                        rs.getString("nama_obat"),
                        rs.getString("satuan"),
                        rs.getInt("total_jumlah"),
                        rs.getBigDecimal("total_nilai")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil laporan obat terpakai: " + e.getMessage());
        }
        return list;
    }

    // =====================================================================
    // 4. RIWAYAT PASIEN (kunjungan + pemeriksaan + resep + resep_detail + pembayaran)
    // =====================================================================

    /** Daftar kunjungan milik seorang pasien, terbaru di atas. */
    public List<Object[]> getKunjunganByPasien(int idPasien) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT k.id_kunjungan, k.tanggal_kunjungan, d.nama_dokter, k.keluhan, k.status "
                + "FROM kunjungan k "
                + "JOIN dokter d ON k.id_dokter = d.id_dokter "
                + "WHERE k.id_pasien = ? "
                + "ORDER BY k.tanggal_kunjungan DESC, k.id_kunjungan DESC";

        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_kunjungan"),
                        rs.getDate("tanggal_kunjungan"),
                        rs.getString("nama_dokter"),
                        rs.getString("keluhan"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil riwayat kunjungan pasien: " + e.getMessage());
        }
        return list;
    }

    /** Data pemeriksaan untuk satu kunjungan (atau null bila belum diperiksa). */
    public Object[] getPemeriksaanByKunjungan(int idKunjungan) {
        String sql = "SELECT id_pemeriksaan, diagnosa, tindakan, catatan, biaya_tindakan "
                + "FROM pemeriksaan WHERE id_kunjungan = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKunjungan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_pemeriksaan"),
                        rs.getString("diagnosa"),
                        rs.getString("tindakan"),
                        rs.getString("catatan"),
                        rs.getBigDecimal("biaya_tindakan")
                    };
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pemeriksaan: " + e.getMessage());
        }
        return null;
    }

    /** Daftar resep (biasanya satu) untuk satu pemeriksaan. */
    public List<Object[]> getResepByPemeriksaan(int idPemeriksaan) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id_resep, tgl_resep FROM resep WHERE id_pemeriksaan = ? ORDER BY id_resep DESC";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPemeriksaan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_resep"),
                        rs.getDate("tgl_resep")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data resep: " + e.getMessage());
        }
        return list;
    }

    /** Detail obat pada satu resep. */
    public List<Object[]> getResepDetailByResep(int idResep) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT o.nama_obat, rd.jumlah, o.satuan, rd.aturan_pakai, rd.harga_satuan, rd.subtotal "
                + "FROM resep_detail rd "
                + "JOIN obat o ON rd.id_obat = o.id_obat "
                + "WHERE rd.id_resep = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idResep);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("nama_obat"),
                        rs.getInt("jumlah"),
                        rs.getString("satuan"),
                        rs.getString("aturan_pakai"),
                        rs.getBigDecimal("harga_satuan"),
                        rs.getBigDecimal("subtotal")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil detail resep: " + e.getMessage());
        }
        return list;
    }

    /** Data pembayaran untuk satu kunjungan (atau null bila belum dibayar). */
    public Object[] getPembayaranByKunjungan(int idKunjungan) {
        String sql = "SELECT id_bayar, tgl_bayar, metode_bayar, total_tindakan, total_obat, total_bayar, kode_pembayaran "
                + "FROM pembayaran WHERE id_kunjungan = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKunjungan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_bayar"),
                        rs.getString("tgl_bayar"),
                        rs.getString("metode_bayar"),
                        rs.getBigDecimal("total_tindakan"),
                        rs.getBigDecimal("total_obat"),
                        rs.getBigDecimal("total_bayar"),
                        rs.getString("kode_pembayaran")
                    };
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data pembayaran: " + e.getMessage());
        }
        return null;
    }
}
