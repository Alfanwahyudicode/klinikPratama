/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
 
import koneksi.Koneksi;
import model.Pembayaran;
 
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
 
public class PembayaranDao {
 

    private static final int BASE_ID_BAYAR = 270000;
    private static final String TABEL_SEQUENCE = "pembayaran_id_sequence";
 
    private void pastikanTabelSequenceAda(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABEL_SEQUENCE + " (id INT PRIMARY KEY, last_id INT NOT NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
 
    private int ambilIdTerbesarSaatIni(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id_bayar) AS id_terbesar FROM pembayaran WHERE id_bayar BETWEEN "
                + BASE_ID_BAYAR + " AND " + (BASE_ID_BAYAR + 9999);
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int idTerbesar = rs.getInt("id_terbesar");
                if (!rs.wasNull() && idTerbesar >= BASE_ID_BAYAR) {
                    return idTerbesar;
                }
            }
        }
        return BASE_ID_BAYAR;
    }

    public int generateIdBayar() {
        try (Connection conn = Koneksi.getKoneksi()) {
            pastikanTabelSequenceAda(conn);
 
            int lastId;
            String cekSql = "SELECT last_id FROM " + TABEL_SEQUENCE + " WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(cekSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lastId = rs.getInt("last_id");
                } else {
                    lastId = ambilIdTerbesarSaatIni(conn);
                    String insertSql = "INSERT INTO " + TABEL_SEQUENCE + " (id, last_id) VALUES (1, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        psIns.setInt(1, lastId);
                        psIns.executeUpdate();
                    }
                }
            }
 
            int idBaru = lastId + 1;
 
            String updateSql = "UPDATE " + TABEL_SEQUENCE + " SET last_id = ? WHERE id = 1";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, idBaru);
                ps.executeUpdate();
            }
 
            return idBaru;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat ID Bayar baru: " + e.getMessage());
            return BASE_ID_BAYAR + 1;
        }
    }
 
    public List<Pembayaran> getAllPembayaran() {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT p.*, k.tanggal_kunjungan, ps.nama_pasien "
                   + "FROM pembayaran p "
                   + "LEFT JOIN kunjungan k ON p.id_kunjungan = k.id_kunjungan "
                   + "LEFT JOIN pasien ps ON k.id_pasien = ps.id_pasien "
                   + "ORDER BY p.id_bayar DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil data pembayaran: " + e.getMessage());
        }
        return list;
    }

    public List<Integer> getIdKunjunganBelumBayar(Integer idKunjunganSaatEdit) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT id_kunjungan FROM kunjungan "
                   + "WHERE id_kunjungan NOT IN (SELECT id_kunjungan FROM pembayaran) "
                   + "ORDER BY id_kunjungan DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(rs.getInt("id_kunjungan"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal mengambil data kunjungan: " + e.getMessage());
        }
 
        if (idKunjunganSaatEdit != null && !list.contains(idKunjunganSaatEdit)) {
            list.add(0, idKunjunganSaatEdit);
        }
        return list;
    }
 
    public List<Object[]> getAllPembayaranTampil() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id_bayar, id_kunjungan, tgl_bayar, total_bayar, "
                   + "metode_bayar, kode_pembayaran "
                   + "FROM pembayaran ORDER BY id_bayar DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_bayar"),
                    rs.getInt("id_kunjungan"),
                    rs.getString("tgl_bayar"),
                    rs.getBigDecimal("total_bayar"),
                    rs.getString("metode_bayar"),
                    rs.getString("kode_pembayaran")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil data pembayaran: " + e.getMessage());
        }
        return list;
    }

    private int angkaDariKeyword(String keyword) {
        String angkaSaja = keyword == null ? "" : keyword.replaceAll("[^0-9]", "");
        if (angkaSaja.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(angkaSaja);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
 
    public List<Pembayaran> cariPembayaran(String keyword) {
        List<Pembayaran> list = new ArrayList<>();
        int idCocok = angkaDariKeyword(keyword);
        String sql = "SELECT p.*, k.tanggal_kunjungan, ps.nama_pasien "
                   + "FROM pembayaran p "
                   + "LEFT JOIN kunjungan k ON p.id_kunjungan = k.id_kunjungan "
                   + "LEFT JOIN pasien ps ON k.id_pasien = ps.id_pasien "
                   + "WHERE ps.nama_pasien LIKE ? "
                   + "OR p.metode_bayar LIKE ? "
                   + "OR p.kode_pembayaran LIKE ? "
                   + "OR p.id_bayar = ? "
                   + "OR p.id_kunjungan = ? "
                   + "ORDER BY p.id_bayar DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setInt(4, idCocok);
            ps.setInt(5, idCocok);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mencari data pembayaran: " + e.getMessage());
        }
        return list;
    }

    public List<Object[]> cariPembayaranTampil(String keyword) {
        List<Object[]> list = new ArrayList<>();
        int idCocok = angkaDariKeyword(keyword);
        String sql = "SELECT p.id_bayar, p.id_kunjungan, p.tgl_bayar, p.total_bayar, "
                   + "p.metode_bayar, p.kode_pembayaran "
                   + "FROM pembayaran p "
                   + "LEFT JOIN kunjungan k ON p.id_kunjungan = k.id_kunjungan "
                   + "LEFT JOIN pasien ps ON k.id_pasien = ps.id_pasien "
                   + "WHERE ps.nama_pasien LIKE ? "
                   + "OR p.metode_bayar LIKE ? "
                   + "OR p.kode_pembayaran LIKE ? "
                   + "OR p.id_bayar = ? "
                   + "OR p.id_kunjungan = ? "
                   + "ORDER BY p.id_bayar DESC";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ps.setInt(4, idCocok);
            ps.setInt(5, idCocok);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_bayar"),
                        rs.getInt("id_kunjungan"),
                        rs.getString("tgl_bayar"),
                        rs.getBigDecimal("total_bayar"),
                        rs.getString("metode_bayar"),
                        rs.getString("kode_pembayaran")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mencari data pembayaran: " + e.getMessage());
        }
        return list;
    }
 
    public Pembayaran getById(int idBayar) {
        Pembayaran p = new Pembayaran();
        String sql = "SELECT * FROM pembayaran WHERE id_bayar = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idBayar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = mapRow(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil data: " + e.getMessage());
        }
        return p;
    }
 
    public String getNamaPasienByKunjungan(int idKunjungan) {
        String nama = "";
        String sql = "SELECT ps.nama_pasien "
                   + "FROM kunjungan k "
                   + "LEFT JOIN pasien ps ON k.id_pasien = ps.id_pasien "
                   + "WHERE k.id_kunjungan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idKunjungan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nama = rs.getString("nama_pasien");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengambil nama pasien: " + e.getMessage());
        }
        return nama;
    }
 
 
    public BigDecimal getTotalTindakanByKunjungan(int idKunjungan) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(biaya_tindakan) AS total "
                   + "FROM pemeriksaan "
                   + "WHERE id_kunjungan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idKunjungan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("total") != null) {
                    total = rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghitung biaya tindakan: " + e.getMessage());
        }
        return total;
    }
 
    public BigDecimal getTotalObatByKunjungan(int idKunjungan) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(rd.subtotal) AS total "
                   + "FROM resep_detail rd "
                   + "LEFT JOIN resep r ON rd.id_resep = r.id_resep "
                   + "LEFT JOIN pemeriksaan p ON r.id_pemeriksaan = p.id_pemeriksaan "
                   + "WHERE p.id_kunjungan = ?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idKunjungan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBigDecimal("total") != null) {
                    total = rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghitung biaya obat: " + e.getMessage());
        }
        return total;
    }

    public boolean kodePembayaranSudahAda(String kodePembayaran) {
        String sql = "SELECT 1 FROM pembayaran WHERE kode_pembayaran = ?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setString(1, kodePembayaran);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengecek kode pembayaran: " + e.getMessage());
            return true;
        }
    }
 
    public boolean tambahPembayaran(Pembayaran p, String metodeBayar, String kodePembayaran) {
        int idBaru = generateIdBayar();
 
        String sql = "INSERT INTO pembayaran "
                   + "(id_bayar, id_kunjungan, total_tindakan, total_obat, "
                   + "total_bayar, metode_bayar, tgl_bayar, kode_pembayaran) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idBaru);
            ps.setInt(2, p.getIdKunjungan());
            ps.setBigDecimal(3, p.getTotalTindakan());
            ps.setBigDecimal(4, p.getTotalObat());
            ps.setBigDecimal(5, p.getTotalBayar());
            ps.setString(6, metodeBayar);
            ps.setString(7, p.getTglBayar());
            ps.setString(8, kodePembayaran);
 
            boolean sukses = ps.executeUpdate() > 0;
            if (sukses) {
                p.setIdBayar(idBaru);
                p.setMetodeBayar(metodeBayar);
                p.setKodePembayaran(kodePembayaran);
            }
            return sukses;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menambah pembayaran: " + e.getMessage());
            return false;
        }
    }
 
    public boolean updatePembayaran(Pembayaran p, String metodeBayar, String kodePembayaran) {
        String sql = "UPDATE pembayaran SET "
                   + "id_kunjungan=?, total_tindakan=?, total_obat=?, "
                   + "total_bayar=?, metode_bayar=?, tgl_bayar=?, kode_pembayaran=? "
                   + "WHERE id_bayar=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, p.getIdKunjungan());
            ps.setBigDecimal(2, p.getTotalTindakan());
            ps.setBigDecimal(3, p.getTotalObat());
            ps.setBigDecimal(4, p.getTotalBayar());
            ps.setString(5, metodeBayar);
            ps.setString(6, p.getTglBayar());
            ps.setString(7, kodePembayaran);
            ps.setInt(8, p.getIdBayar());
 
            boolean sukses = ps.executeUpdate() > 0;
            if (sukses) {
                p.setMetodeBayar(metodeBayar);
                p.setKodePembayaran(kodePembayaran);
            }
            return sukses;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal mengupdate pembayaran: " + e.getMessage());
            return false;
        }
    }
 
    public boolean hapusPembayaran(int idBayar) {
        String sql = "DELETE FROM pembayaran WHERE id_bayar=?";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
 
            ps.setInt(1, idBayar);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghapus pembayaran: " + e.getMessage());
            return false;
        }
    }
    
    public BigDecimal getTotalPendapatan() {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(total_bayar) AS total FROM pembayaran";
 
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            if (rs.next() && rs.getBigDecimal("total") != null) {
                total = rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal menghitung total pendapatan: " + e.getMessage());
        }
        return total;
    }
 
    private Pembayaran mapRow(ResultSet rs) throws SQLException {
        Pembayaran p = new Pembayaran();
        p.setIdBayar(rs.getInt("id_bayar"));
        p.setIdKunjungan(rs.getInt("id_kunjungan"));
        p.setTotalTindakan(rs.getBigDecimal("total_tindakan"));
        p.setTotalObat(rs.getBigDecimal("total_obat"));
        p.setTotalBayar(rs.getBigDecimal("total_bayar"));
        p.setTglBayar(rs.getString("tgl_bayar"));
        p.setMetodeBayar(rs.getString("metode_bayar"));
        p.setKodePembayaran(rs.getString("kode_pembayaran"));
        return p;
    }
}