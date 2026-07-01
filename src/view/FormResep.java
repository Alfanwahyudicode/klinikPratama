/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.ObatDao;
import dao.PemeriksaanDao;
import dao.ResepDao;
import dao.ResepDetailDao;
import model.Obat;
import model.Pemeriksaan;
import model.Resep;
import model.ResepDetail;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author VanZ
 */
public class FormResep extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormResep.class.getName());

    private final ResepDao       resepDao       = new ResepDao();
    private final ResepDetailDao detailDao      = new ResepDetailDao();
    private final ObatDao        obatDao        = new ObatDao();
    private final PemeriksaanDao pemeriksaanDao = new PemeriksaanDao();

    private List<Pemeriksaan> listPemeriksaan;
    private List<Obat> listObat;

    private Resep resepAktif = null;
    private List<ResepDetail> listDetail = new ArrayList<>();
    private List<Resep> listResepMaster = new ArrayList<>();

    // Baris detail (obat) yang sedang diedit dari tabel bawah. null = mode tambah baru.
    private ResepDetail detailAktif = null;

    // Flag supaya listener tidak saling memicu ulang saat kita mengisi field secara program.
    private boolean sedangMengisiOtomatis = false;

    /**
     * Creates new form FormResep
     */
    public FormResep() {
        initComponents();
        setupTabel();
        setupTabelDetail();
        loadComboPemeriksaan();
        loadComboObat();
        loadMasterTable();

        txtTglResep.setText(LocalDate.now().toString());

        txtIdResep.setEditable(false);
        txtSubtotal.setEditable(false);
        txtCatatanPemerisaan.setEditable(false);
        txtNamaObat.setEditable(false);
        txtStok.setEditable(false);
        txtHarga.setEditable(false);
        jTextField1.setEditable(false);

        txtSubtotal.setText("Rp 0");
        txtCatatanPemerisaan.setText("");

        txtJumlah.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { hitungTotalHargaBaris(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { hitungTotalHargaBaris(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotalHargaBaris(); }
        });

        resetFormPenuh();
    }

    private void setupTabel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID Resep", "ID Pemeriksaan", "Tanggal Resep", "SubTotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        jTable1.setModel(model);
        jTable1.getTableHeader().setReorderingAllowed(false);

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelMouseClicked(evt);
            }
        });
    }

    private void setupTabelDetail() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No", "ID Obat", "Nama", "Total Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        jTable2.setModel(model);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDetailMouseClicked(evt);
            }
        });
    }

    private void loadComboObat() {
        listObat = obatDao.getAllObat();
        cmbIdObat.removeAllItems();

        if (listObat.isEmpty()) {
            cmbIdObat.addItem("Tidak ada data obat");
        } else {
            for (Obat o : listObat) {
                cmbIdObat.addItem(o.getIdObat() + " - " + o.getNamaObat());
            }
        }
        isiDataObatTerpilih();
    }

    private void isiDataObatTerpilih() {
        sedangMengisiOtomatis = true;
        Obat o = getObatTerpilih();
        if (o != null) {
            txtNamaObat.setText(o.getNamaObat());
            txtStok.setText(String.valueOf(o.getStok()));
            txtHarga.setText(formatRupiah(o.getHargaJual()));
        } else {
            txtNamaObat.setText("");
            txtStok.setText("");
            txtHarga.setText("");
        }
        sedangMengisiOtomatis = false;
        hitungTotalHargaBaris();
    }

    private void loadComboPemeriksaan() {
        listPemeriksaan = pemeriksaanDao.getAllPemeriksaan();
        jComboBox1.removeAllItems();

        if (listPemeriksaan.isEmpty()) {
            jComboBox1.addItem("Tidak ada data pemeriksaan");
        } else {
            for (Pemeriksaan p : listPemeriksaan) {
                jComboBox1.addItem(p.getIdPemeriksaan() + " - " + p.getDiagnosa());
            }
        }
    }

    private void loadMasterTable() {
        listResepMaster = resepDao.getAllResep();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        for (Resep r : listResepMaster) {
            BigDecimal total = detailDao.getTotalByResepId(r.getIdResep());
            model.addRow(new Object[]{
                formatIdResep(r.getIdResep()),
                r.getIdPemeriksaan(),
                r.getTglResep() != null ? r.getTglResep().toString() : "",
                formatRupiah(total)
            });
        }
    }

    private String formatIdResep(int idResep) {
        return String.valueOf(idResep);
    }

    private String formatRupiah(BigDecimal angka) {
        if (angka == null) return "Rp 0";
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(
            new java.util.Locale("id", "ID"));
        return format.format(angka);
    }

    private BigDecimal parseRupiah(String teks) {
        if (teks == null) return null;
        String bersih = teks.replace("Rp", "").replace(".", "").replace(",", ".").trim();
        if (bersih.isEmpty()) return null;
        try {
            return new BigDecimal(bersih);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String cariNamaObat(int idObat) {
        if (listObat == null) return String.valueOf(idObat);
        for (Obat o : listObat) {
            if (o.getIdObat() == idObat) return o.getNamaObat();
        }
        return String.valueOf(idObat);
    }

    private Obat cariObatById(int idObat) {
        if (listObat == null) return null;
        for (Obat o : listObat) {
            if (o.getIdObat() == idObat) return o;
        }
        return null;
    }

    private Obat getObatTerpilih() {
        int idx = cmbIdObat.getSelectedIndex();
        if (listObat != null && idx >= 0 && idx < listObat.size()) {
            return listObat.get(idx);
        }
        return null;
    }

    private void pilihObatPadaCombo(int idObat) {
        if (listObat == null) return;
        for (int i = 0; i < listObat.size(); i++) {
            if (listObat.get(i).getIdObat() == idObat) {
                cmbIdObat.setSelectedIndex(i);
                return;
            }
        }
    }

    private Pemeriksaan getPemeriksaanTerpilih() {
        int idx = jComboBox1.getSelectedIndex();
        if (listPemeriksaan != null && idx >= 0 && idx < listPemeriksaan.size()) {
            return listPemeriksaan.get(idx);
        }
        return null;
    }

    private void hitungTotalHargaBaris() {
        if (sedangMengisiOtomatis) return;
        try {
            int jumlah = Integer.parseInt(txtJumlah.getText().trim());
            BigDecimal harga = parseRupiah(txtHarga.getText());
            if (harga == null) {
                jTextField1.setText("Rp 0");
                return;
            }
            BigDecimal total = harga.multiply(BigDecimal.valueOf(jumlah));
            jTextField1.setText(formatRupiah(total));
        } catch (NumberFormatException e) {
            jTextField1.setText("Rp 0");
        }
    }


    private void refreshTotalResep() {
        if (resepAktif == null) {
            listDetail = new ArrayList<>();
            loadTabelDetail();
            txtSubtotal.setText("Rp 0");
            return;
        }
        listDetail = detailDao.getByResepId(resepAktif.getIdResep());
        loadTabelDetail();
        BigDecimal total = detailDao.getTotalByResepId(resepAktif.getIdResep());
        txtSubtotal.setText(formatRupiah(total));
    }

    private void loadTabelDetail() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        int no = 1;
        for (ResepDetail rd : listDetail) {
            model.addRow(new Object[]{
                no++,
                rd.getIdObat(),
                cariNamaObat(rd.getIdObat()),
                formatRupiah(rd.getSubtotal())
            });
        }
    }

    private void setDetailEnabled(boolean enabled) {
        cmbIdObat.setEnabled(enabled);
        txtJumlah.setEnabled(enabled);
        txtAturanPakai.setEnabled(enabled);
        btnTambahObat.setEnabled(enabled);
        btnHapusObat.setEnabled(enabled);
    }

    private void bersihkanForm() {
        detailAktif = null;
        btnTambahObat.setText("Tambah Obat");
        txtJumlah.setText("");
        txtAturanPakai.setText("");
        if (listObat != null && !listObat.isEmpty()) cmbIdObat.setSelectedIndex(0);
        isiDataObatTerpilih();
    }


    private void muatResepUntukPemeriksaanTerpilih() {
        Pemeriksaan pe = getPemeriksaanTerpilih();
        if (pe == null) {
            resepAktif = null;
            listDetail = new ArrayList<>();
            txtIdResep.setText("(otomatis)");
            txtTglResep.setText(LocalDate.now().toString());
            txtCatatanPemerisaan.setText("");
            jTable1.clearSelection();
            setDetailEnabled(false);
            bersihkanForm();
            loadTabelDetail();
            txtSubtotal.setText("Rp 0");
            return;
        }

        txtCatatanPemerisaan.setText(pe.getCatatan() != null ? pe.getCatatan() : "");

        Resep existing = resepDao.getByIdPemeriksaan(pe.getIdPemeriksaan());
        if (existing != null) {
            resepAktif = existing;
            txtIdResep.setText(formatIdResep(existing.getIdResep()));
            txtTglResep.setText(existing.getTglResep() != null
                ? existing.getTglResep().toString() : LocalDate.now().toString());
            setDetailEnabled(true);
            bersihkanForm();
            refreshTotalResep();

            if (listResepMaster != null) {
                for (int i = 0; i < listResepMaster.size(); i++) {
                    if (listResepMaster.get(i).getIdResep() == existing.getIdResep()) {
                        jTable1.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        } else {
            resepAktif = null;
            listDetail = new ArrayList<>();
            txtIdResep.setText("(otomatis)");
            txtTglResep.setText(LocalDate.now().toString());
            jTable1.clearSelection();
            setDetailEnabled(false);
            bersihkanForm();
            loadTabelDetail();
            txtSubtotal.setText("Rp 0");
        }
    }

    private void resetFormPenuh() {
        resepAktif = null;
        detailAktif = null;
        listDetail = new ArrayList<>();
        jTable1.clearSelection();
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
        muatResepUntukPemeriksaanTerpilih();
    }

    private boolean validasiDetail() {
        if (resepAktif == null) {
            JOptionPane.showMessageDialog(this,
                "Simpan Resep (Header) terlebih dahulu sebelum menambah obat!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (getObatTerpilih() == null) {
            JOptionPane.showMessageDialog(this, "Pilih obat terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            int j = Integer.parseInt(txtJumlah.getText().trim());
            if (j <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka positif!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtAturanPakai.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aturan Pakai tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void simpanResepHeader() {
        Pemeriksaan pe = getPemeriksaanTerpilih();
        if (pe == null) {
            JOptionPane.showMessageDialog(this, "Pilih ID Pemeriksaan terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tglStr = txtTglResep.getText().trim();
        Date tglResep;
        try {
            tglResep = Date.valueOf(tglStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Format tanggal salah! Gunakan: yyyy-MM-dd", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (resepAktif == null) {
            // ===== INSERT RESEP BARU (ID Resep dibuat otomatis & sekali pakai) =====
            Resep r = new Resep(pe.getIdPemeriksaan(), tglResep);
            int idBaru = resepDao.tambahResep(r);

            if (idBaru > 0) {
                resepAktif = r;
                listDetail = new ArrayList<>();
                txtIdResep.setText(formatIdResep(idBaru));
                txtCatatanPemerisaan.setText(pe.getCatatan() != null ? pe.getCatatan() : "");
                setDetailEnabled(true);
                bersihkanForm();
                refreshTotalResep();
                loadMasterTable();
                JOptionPane.showMessageDialog(this,
                    "Resep baru berhasil disimpan! ID Resep: " + formatIdResep(idBaru)
                    + "\nSekarang tambahkan obat pada bagian Detail Resep Obat.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            resepAktif.setIdPemeriksaan(pe.getIdPemeriksaan());
            resepAktif.setTglResep(tglResep);

            if (resepDao.updateResep(resepAktif)) {
                txtCatatanPemerisaan.setText(pe.getCatatan() != null ? pe.getCatatan() : "");
                setDetailEnabled(true);
                loadMasterTable();
                JOptionPane.showMessageDialog(this,
                    "Resep " + formatIdResep(resepAktif.getIdResep()) + " berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void tambahObatKeResep() {
        if (!validasiDetail()) return;

        Obat obatDipilih = getObatTerpilih();
        int jumlahBaru = Integer.parseInt(txtJumlah.getText().trim());
        BigDecimal harga = parseRupiah(txtHarga.getText());
        if (harga == null) {
            JOptionPane.showMessageDialog(this, "Harga Satuan tidak valid!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal subtotalBaris = harga.multiply(BigDecimal.valueOf(jumlahBaru));
        String aturanPakai = txtAturanPakai.getText().trim();

        if (detailAktif == null) {
            if (jumlahBaru > obatDipilih.getStok()) {
                JOptionPane.showMessageDialog(this,
                    "Stok obat tidak cukup! Stok tersedia: " + obatDipilih.getStok(),
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResepDetail rd = new ResepDetail(
                resepAktif.getIdResep(),
                obatDipilih.getIdObat(),
                jumlahBaru,
                aturanPakai,
                harga,
                subtotalBaris
            );

            int idDetail = detailDao.tambahDetail(rd);
            if (idDetail > 0) {
                obatDao.updateStok(obatDipilih.getIdObat(), obatDipilih.getStok() - jumlahBaru);
                loadComboObat();
                bersihkanForm();
                refreshTotalResep();
                loadMasterTable();
                JOptionPane.showMessageDialog(this,
                    "Obat berhasil ditambahkan ke resep " + formatIdResep(resepAktif.getIdResep()) + ".",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            int idObatLama = detailAktif.getIdObat();
            int jumlahLama = detailAktif.getJumlah();

            Obat obatLama = cariObatById(idObatLama);
            int stokTersediaUntukObatBaru = obatDipilih.getStok();
            if (obatLama != null && obatLama.getIdObat() == obatDipilih.getIdObat()) {
                stokTersediaUntukObatBaru += jumlahLama;
            }

            if (jumlahBaru > stokTersediaUntukObatBaru) {
                JOptionPane.showMessageDialog(this,
                    "Stok obat tidak cukup! Stok tersedia: " + stokTersediaUntukObatBaru,
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            detailAktif.setIdObat(obatDipilih.getIdObat());
            detailAktif.setJumlah(jumlahBaru);
            detailAktif.setAturanPakai(aturanPakai);
            detailAktif.setHargaSatuan(harga);
            detailAktif.setSubtotal(subtotalBaris);

            if (detailDao.updateDetail(detailAktif)) {
                if (obatLama != null) {
                    obatDao.updateStok(obatLama.getIdObat(), obatLama.getStok() + jumlahLama);
                }
                Obat obatBaruTerkini = obatDao.getAllObat().stream()
                        .filter(o -> o.getIdObat() == obatDipilih.getIdObat())
                        .findFirst().orElse(obatDipilih);
                obatDao.updateStok(obatBaruTerkini.getIdObat(), obatBaruTerkini.getStok() - jumlahBaru);

                loadComboObat();
                bersihkanForm();
                refreshTotalResep();
                loadMasterTable();
                JOptionPane.showMessageDialog(this,
                    "Data obat pada resep berhasil diperbarui.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void hapusObatDariResep() {
        if (resepAktif == null) {
            JOptionPane.showMessageDialog(this, "Pilih / simpan resep terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = jTable2.getSelectedRow();
        if (row < 0 || listDetail == null || row >= listDetail.size()) {
            JOptionPane.showMessageDialog(this,
                "Pilih baris obat pada tabel detail yang ingin dihapus terlebih dahulu.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResepDetail target = listDetail.get(row);

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus obat \"" + cariNamaObat(target.getIdObat()) + "\" dari resep ini?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            Obat o = cariObatById(target.getIdObat());
            if (o != null) {
                obatDao.updateStok(o.getIdObat(), o.getStok() + target.getJumlah());
            }

            if (detailDao.hapusDetail(target.getIdResepDetail())) {
                loadComboObat();
                bersihkanForm();
                refreshTotalResep();
                loadMasterTable();
                JOptionPane.showMessageDialog(this, "Obat berhasil dihapus dari resep!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void hapusResepHeader() {
        if (resepAktif == null) {
            JOptionPane.showMessageDialog(this, "Pilih resep yang ingin dihapus terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus resep " + formatIdResep(resepAktif.getIdResep())
            + " beserta seluruh obat di dalamnya?\nStok obat yang sudah dipakai akan dikembalikan.",
            "Konfirmasi Hapus Resep", JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            List<ResepDetail> details = detailDao.getByResepId(resepAktif.getIdResep());
            for (ResepDetail rd : details) {
                Obat o = cariObatById(rd.getIdObat());
                if (o != null) {
                    obatDao.updateStok(o.getIdObat(), o.getStok() + rd.getJumlah());
                }
            }

            if (resepDao.hapusResep(resepAktif.getIdResep())) {
                loadComboObat();
                loadMasterTable();
                resetFormPenuh();
                JOptionPane.showMessageDialog(this, "Resep berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {
        int row = jTable1.getSelectedRow();
        if (row < 0 || listResepMaster == null || row >= listResepMaster.size()) return;

        Resep r = listResepMaster.get(row);

        if (listPemeriksaan != null) {
            for (int i = 0; i < listPemeriksaan.size(); i++) {
                if (listPemeriksaan.get(i).getIdPemeriksaan() == r.getIdPemeriksaan()) {
                    if (jComboBox1.getSelectedIndex() == i) {
                        muatResepUntukPemeriksaanTerpilih();
                    } else {
                        jComboBox1.setSelectedIndex(i); // akan memicu jComboBox1ActionPerformed
                    }
                    return;
                }
            }
        }
    }

    private void tabelDetailMouseClicked(java.awt.event.MouseEvent evt) {
        int row = jTable2.getSelectedRow();
        if (row < 0 || listDetail == null || row >= listDetail.size()) return;

        ResepDetail rd = listDetail.get(row);
        detailAktif = rd;

        sedangMengisiOtomatis = true;
        pilihObatPadaCombo(rd.getIdObat());
        Obat o = cariObatById(rd.getIdObat());
        if (o != null) {
            txtNamaObat.setText(o.getNamaObat());
            txtHarga.setText(formatRupiah(rd.getHargaSatuan()));
            txtStok.setText(String.valueOf(o.getStok() + rd.getJumlah()));
        }
        txtJumlah.setText(String.valueOf(rd.getJumlah()));
        txtAturanPakai.setText(rd.getAturanPakai());
        sedangMengisiOtomatis = false;

        jTextField1.setText(formatRupiah(rd.getSubtotal()));
        btnTambahObat.setText("Update Obat");
    }

    private void cariData() {
        String keyword = txtCari.getText().trim();

        if (keyword.isEmpty()) {
            loadMasterTable();
            return;
        }

        if (listResepMaster == null) {
            loadMasterTable();
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        boolean ada = false;
        for (Resep r : listResepMaster) {
            String idResepStr = formatIdResep(r.getIdResep());
            String idPemeriksaanStr = String.valueOf(r.getIdPemeriksaan());
            String tglStr = r.getTglResep() != null ? r.getTglResep().toString() : "";

            boolean cocok =
                idResepStr.toLowerCase().contains(keyword.toLowerCase()) ||
                idPemeriksaanStr.contains(keyword) ||
                tglStr.contains(keyword);

            if (cocok) {
                BigDecimal total = detailDao.getTotalByResepId(r.getIdResep());
                model.addRow(new Object[]{idResepStr, r.getIdPemeriksaan(), tglStr, formatRupiah(total)});
                ada = true;
            }
        }

        if (!ada) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Info",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdResep = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtTglResep = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        txtAturanPakai = new javax.swing.JTextField();
        cmbIdObat = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtCatatanPemerisaan = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnTambahObat = new javax.swing.JButton();
        btnHapusObat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        txtNamaObat = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("F O R M U L I R   R E S E P   O B A T");

        jLabel2.setText("ID Resep");

        jLabel3.setText("ID Pemeriksaan");

        txtIdResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdResepActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Tangga Resep");

        txtTglResep.setText("yyyy-MM-dd");
        txtTglResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTglResepActionPerformed(evt);
            }
        });

        jLabel5.setText("ID Obat");

        jLabel6.setText("Jumlah");

        txtJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJumlahActionPerformed(evt);
            }
        });

        jLabel7.setText("Stok Obat");

        jLabel8.setText("Aturan Pakai");

        jLabel9.setText("SubTotal");

        txtSubtotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubtotalActionPerformed(evt);
            }
        });

        txtAturanPakai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAturanPakaiActionPerformed(evt);
            }
        });

        cmbIdObat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbIdObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIdObatActionPerformed(evt);
            }
        });

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });

        btnCari.setText("Cari Resep");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        jLabel10.setText("Catatan Pemeriksaan");

        txtCatatanPemerisaan.setText("Catatan....");
        txtCatatanPemerisaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCatatanPemerisaanActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("DETAIL RESEP OBAT");

        btnTambahObat.setText("Tambah Obat");
        btnTambahObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahObatActionPerformed(evt);
            }
        });

        btnHapusObat.setText("Hapus Obat");
        btnHapusObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusObatActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Resep", "ID Pemeriksaan", "Tanggal Resep", "SubTotal"
            }
        ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jLabel12.setText("NamaObat");

        txtNamaObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaObatActionPerformed(evt);
            }
        });

        jLabel13.setText("Harga Satuan");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No", "ID", "Nama", "Total Harga"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel14.setText("Total Harga");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtSubtotal)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cmbIdObat, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(121, 121, 121)))
                                    .addComponent(txtAturanPakai, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnTambahObat, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNamaObat, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                            .addComponent(txtHarga, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)))))
                            .addComponent(txtCatatanPemerisaan)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHapusObat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTglResep, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtIdResep))))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCari))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtIdResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTglResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCatatanPemerisaan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbIdObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAturanPakai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnTambahObat)
                                    .addComponent(btnHapusObat)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNamaObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSimpan)
                            .addComponent(btnHapus)
                            .addComponent(btnBatal)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdResepActionPerformed

    private void txtSubtotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubtotalActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txtSubtotalActionPerformed

    private void txtJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahActionPerformed

    private void txtTglResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTglResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglResepActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        simpanResepHeader();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        muatResepUntukPemeriksaanTerpilih();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        hapusResepHeader();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        resetFormPenuh();
        loadMasterTable();
        JOptionPane.showMessageDialog(this, "Form berhasil direset.",
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        cariData();
    }//GEN-LAST:event_btnCariActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
         cariData();
    }//GEN-LAST:event_txtCariActionPerformed

    private void cmbIdObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdObatActionPerformed
        // TODO add your handling code here:
        isiDataObatTerpilih();
    }//GEN-LAST:event_cmbIdObatActionPerformed

    private void txtAturanPakaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAturanPakaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAturanPakaiActionPerformed

    private void txtCatatanPemerisaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCatatanPemerisaanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCatatanPemerisaanActionPerformed

    private void btnTambahObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahObatActionPerformed
        // TODO add your handling code here:
        tambahObatKeResep();
    }//GEN-LAST:event_btnTambahObatActionPerformed

    private void btnHapusObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusObatActionPerformed
        // TODO add your handling code here:
        hapusObatDariResep();
    }//GEN-LAST:event_btnHapusObatActionPerformed

    private void txtNamaObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaObatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaObatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FormResep().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnHapusObat;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahObat;
    private javax.swing.JComboBox<String> cmbIdObat;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField txtAturanPakai;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCatatanPemerisaan;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdResep;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtNamaObat;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTglResep;
    // End of variables declaration//GEN-END:variables
}