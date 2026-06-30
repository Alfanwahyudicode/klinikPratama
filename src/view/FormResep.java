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
    private ResepDetail detailTerpilih = null;
    private List<ResepDetail> listDetail;
    /**
     * Creates new form FormResep
     */
    public FormResep() {
        initComponents();
        setupTabel();
        loadComboPemeriksaan();
        loadComboObat();
        txtTglResep.setText(LocalDate.now().toString());
        txtIdResep.setEditable(false);
        txtSubtotal.setEditable(false);
        
        txtJumlah.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e)  { hitungSubtotal(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e)  { hitungSubtotal(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungSubtotal(); }
});

// Listener otomatis hitung subtotal saat txtHargaSatuan diketik
txtHargaSatuan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    public void insertUpdate(javax.swing.event.DocumentEvent e)  { hitungSubtotal(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e)  { hitungSubtotal(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungSubtotal(); }
});
    }
    private void setupTabel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"No", "ID Resep", "ID Obat", "Jumlah", "Aturan Pakai", "Harga", "Subtotal"}, 0) {
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
    
        private void loadComboObat() {
        listObat = obatDao.getAllObat();
        cmbIdObat.removeAllItems();
 
        if (listObat.isEmpty()) {
            cmbIdObat.addItem("Tidak ada data obat");
        } else {
            for (Obat o : listObat) {
                cmbIdObat.addItem(o.toString());
            }
        }
        isiHargaDariObatTerpilih();
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
    
    private void isiHargaDariObatTerpilih() {
    int idx = cmbIdObat.getSelectedIndex();
    if (listObat != null && idx >= 0 && idx < listObat.size()) {
        Obat o = listObat.get(idx);
        BigDecimal harga = o.getHargaJual();
        txtHargaSatuan.setText(harga != null ? formatRupiah(harga) : "Rp 0");
        hitungSubtotal();
    }
}
 
    private void hitungSubtotal() {
    try {
        int jumlah = Integer.parseInt(txtJumlah.getText().trim());

        // Bersihkan format rupiah dulu sebelum diparse
        String hargaBersih = txtHargaSatuan.getText()
            .replace("Rp", "").replace(".", "").replace(",", ".").trim();
        BigDecimal harga = new BigDecimal(hargaBersih);
        BigDecimal sub   = harga.multiply(BigDecimal.valueOf(jumlah));

        txtSubtotal.setText(formatRupiah(sub));
    } catch (NumberFormatException e) {
        txtSubtotal.setText("Rp 0");
    }
}
    private String formatRupiah(BigDecimal angka) {
    if (angka == null) return "Rp 0";
    java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(
        new java.util.Locale("id", "ID"));
    return format.format(angka);
}

    
    private void loadTabelDetail() {
    if (resepAktif == null) return;

    listDetail = detailDao.getByResepId(resepAktif.getIdResep());
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    int no = 1;
    for (ResepDetail rd : listDetail) {
        String namaObat = cariNamaObat(rd.getIdObat());
        model.addRow(new Object[]{
            no++,
            rd.getIdResep(),
            namaObat,
            rd.getJumlah(),
            rd.getAturanPakai(),
            formatRupiah(rd.getHargaSatuan()),   // ← format rupiah
            formatRupiah(rd.getSubtotal())        // ← format rupiah
        });
    }
}
    
    private String cariNamaObat(int idObat) {
        if (listObat == null) return String.valueOf(idObat);
        for (Obat o : listObat) {
            if (o.getIdObat() == idObat) return o.getNamaObat();
        }
        return String.valueOf(idObat);
    }
 
    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {
    int row = jTable1.getSelectedRow();
    if (row >= 0 && listDetail != null && row < listDetail.size()) {
        detailTerpilih = listDetail.get(row);

        if (listObat != null) {
            for (int i = 0; i < listObat.size(); i++) {
                if (listObat.get(i).getIdObat() == detailTerpilih.getIdObat()) {
                    cmbIdObat.setSelectedIndex(i);
                    break;
                }
            }
        }

        txtJumlah.setText(String.valueOf(detailTerpilih.getJumlah()));
        txtAturanPakai.setText(detailTerpilih.getAturanPakai());
        txtHargaSatuan.setText(detailTerpilih.getHargaSatuan() != null
                ? formatRupiah(detailTerpilih.getHargaSatuan()) : "Rp 0");
        txtSubtotal.setText(detailTerpilih.getSubtotal() != null
                ? formatRupiah(detailTerpilih.getSubtotal()) : "Rp 0");
    }
}
    
    private void bersihkanForm() {
        txtJumlah.setText("");
        txtAturanPakai.setText("");
        txtHargaSatuan.setText("");
        txtSubtotal.setText("0");
        jTable1.clearSelection();
        detailTerpilih = null;
        if (listObat != null && !listObat.isEmpty()) cmbIdObat.setSelectedIndex(0);
        isiHargaDariObatTerpilih();
    }
    
    private boolean validasiDetail() {
        if (resepAktif == null) {
            JOptionPane.showMessageDialog(this,
                "Simpan Resep (Header) terlebih dahulu sebelum menambah obat!",
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
        if (txtHargaSatuan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga Satuan tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private Obat getObatTerpilih() {
        int idx = cmbIdObat.getSelectedIndex();
        if (listObat != null && idx >= 0 && idx < listObat.size()) {
            return listObat.get(idx);
        }
        return null;
    }
    
    private Pemeriksaan getPemeriksaanTerpilih() {
        int idx = jComboBox1.getSelectedIndex();
        if (listPemeriksaan != null && idx >= 0 && idx < listPemeriksaan.size()) {
            return listPemeriksaan.get(idx);
        }
        return null;
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
 
        Resep r = new Resep(pe.getIdPemeriksaan(), tglResep);
        int idBaru = resepDao.tambahResep(r);
 
        if (idBaru > 0) {
            resepAktif = r;
            txtIdResep.setText(String.valueOf(idBaru));
            JOptionPane.showMessageDialog(this,
                "Resep berhasil disimpan! ID Resep: " + idBaru + "\nSekarang tambahkan obat.",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cariData() {
    String keyword = txtCari.getText().trim();

    if (resepAktif == null) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Pilih ID Pemeriksaan terlebih dahulu!", "Peringatan",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (keyword.isEmpty()) {
        loadTabelDetail();
        return;
    }

    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    int no = 1;
    for (ResepDetail rd : listDetail) {
        String namaObat = cariNamaObat(rd.getIdObat());

        boolean cocok =
            String.valueOf(rd.getIdResep()).contains(keyword) ||
            String.valueOf(rd.getIdObat()).contains(keyword)  ||
            namaObat.toLowerCase().contains(keyword.toLowerCase()) ||
            (rd.getAturanPakai() != null &&
             rd.getAturanPakai().toLowerCase().contains(keyword.toLowerCase()));

        if (cocok) {
            model.addRow(new Object[]{
                no++,
                rd.getIdResep(),
                namaObat,
                rd.getJumlah(),
                rd.getAturanPakai(),
                rd.getHargaSatuan(),
                rd.getSubtotal()
            });
        }
    }

    if (model.getRowCount() == 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Data tidak ditemukan!", "Info",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        loadTabelDetail();
    }

    if (model.getRowCount() == 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Data tidak ditemukan!", "Info",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        loadTabelDetail();
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
        txtAturanPakai = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        txtHargaSatuan = new javax.swing.JTextField();
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

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("   Formulir Resep Obat");

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

        jLabel7.setText("Harga Satuan");

        jLabel8.setText("Aturan Pakai");

        jLabel9.setText("SubTotal");

        txtSubtotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubtotalActionPerformed(evt);
            }
        });

        txtHargaSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaSatuanActionPerformed(evt);
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

        txtCari.setText("Cari Resep.....");
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

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("   Detail Resep Obat");

        btnTambahObat.setText("Tambah Obat");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(438, 438, 438))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtAturanPakai)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbIdObat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(txtCatatanPemerisaan, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnTambahObat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGap(32, 32, 32)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnHapusObat, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtSubtotal, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(30, 30, 30)
                                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                    .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTglResep, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                                    .addComponent(txtIdResep)))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCari))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtIdResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtTglResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCatatanPemerisaan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbIdObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAturanPakai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnTambahObat))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(txtHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapusObat)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBatal)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnSimpan)
                                .addComponent(btnHapus)))))
                .addContainerGap(329, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdResepActionPerformed

    private void txtSubtotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubtotalActionPerformed
        // TODO add your handling code here:
        hitungSubtotal();
    }//GEN-LAST:event_txtSubtotalActionPerformed

    private void txtJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahActionPerformed

    private void txtTglResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTglResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTglResepActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        if (resepAktif == null) {
            simpanResepHeader();
            if (resepAktif == null) return;
        }
 
        if (!validasiDetail()) return;
 
        Obat obatDipilih = getObatTerpilih();
        if (obatDipilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih obat terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int jumlah = Integer.parseInt(txtJumlah.getText().trim());
        if (jumlah > obatDipilih.getStok()) {
            JOptionPane.showMessageDialog(this,
                "Stok obat tidak cukup! Stok tersedia: " + obatDipilih.getStok(),
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String hargaBersih = txtHargaSatuan.getText()
            .replace("Rp", "").replace(".", "").replace(",", ".").trim();
        BigDecimal harga = new BigDecimal(hargaBersih);        BigDecimal subtotal = harga.multiply(BigDecimal.valueOf(jumlah));
 
        ResepDetail rd = new ResepDetail(
            resepAktif.getIdResep(),
            obatDipilih.getIdObat(),
            jumlah,
            txtAturanPakai.getText().trim(),
            harga,
            subtotal
        );
 
        int idDetail = detailDao.tambahDetail(rd);
        if (idDetail > 0) {
            obatDao.updateStok(obatDipilih.getIdObat(), obatDipilih.getStok() - jumlah);
            loadComboObat();
            loadTabelDetail();
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Obat berhasil ditambahkan ke resep!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        Pemeriksaan pe = getPemeriksaanTerpilih();
        if (pe == null) return;
 
        Resep existing = resepDao.getByIdPemeriksaan(pe.getIdPemeriksaan());
        if (existing != null) {
            resepAktif = existing;
            txtIdResep.setText(String.valueOf(existing.getIdResep()));
            txtTglResep.setText(existing.getTglResep() != null
                ? existing.getTglResep().toString() : LocalDate.now().toString());
            loadTabelDetail();
            JOptionPane.showMessageDialog(this,
                "Pemeriksaan ini sudah memiliki resep (ID: " + existing.getIdResep() + ").\nDetail obat ditampilkan di tabel.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            resepAktif = null;
            txtIdResep.setText("");
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        if (detailTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih baris pada tabel terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus data obat ini dari resep?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
 
        if (konfirmasi == JOptionPane.YES_OPTION) {
            Obat o = cariObatById(detailTerpilih.getIdObat());
            if (o != null) {
                obatDao.updateStok(o.getIdObat(), o.getStok() + detailTerpilih.getJumlah());
            }
 
            if (detailDao.hapusDetail(detailTerpilih.getIdResepDetail())) {
                loadComboObat();
                loadTabelDetail();
                bersihkanForm();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        bersihkanForm();
        resepAktif = null;
        detailTerpilih = null;
        txtIdResep.setText("");
        txtTglResep.setText(LocalDate.now().toString());
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Form berhasil direset.",
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        txtCari.setText("");
    if (resepAktif != null) {
        loadTabelDetail();
    }//GEN-LAST:event_btnCariActionPerformed

}
    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
         cariData();
    }//GEN-LAST:event_txtCariActionPerformed

    private void cmbIdObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdObatActionPerformed
        // TODO add your handling code here:
        isiHargaDariObatTerpilih();
    }//GEN-LAST:event_cmbIdObatActionPerformed

    private void txtHargaSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaSatuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaSatuanActionPerformed

    private void txtCatatanPemerisaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCatatanPemerisaanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCatatanPemerisaanActionPerformed

    private void btnHapusObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusObatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusObatActionPerformed
    private Obat cariObatById(int idObat) {
        if (listObat == null) return null;
        for (Obat o : listObat) {
            if (o.getIdObat() == idObat) return o;
        }
        return null;
    }
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtAturanPakai;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCatatanPemerisaan;
    private javax.swing.JTextField txtHargaSatuan;
    private javax.swing.JTextField txtIdResep;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTglResep;
    // End of variables declaration//GEN-END:variables
}
