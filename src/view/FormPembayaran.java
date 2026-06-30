/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.PembayaranDao;
import model.Pembayaran;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

/**
 * @author VanZ
 */
public class FormPembayaran extends javax.swing.JFrame {

   private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPembayaran.class.getName());
    private PembayaranDao pembayaranDao = new PembayaranDao();
 
    public FormPembayaran(int idKunjunganEksternal) {
    initComponents();
    kosongkanForm();
    tampilkanData();
    tambahListenerAutoHitung();

    cmbIdKunjungan.setSelectedItem(String.valueOf(idKunjunganEksternal));
    cariDataKunjungan();
}
    
    private void muatComboIdKunjungan() {
        cmbIdKunjungan.removeAllItems();
        List<Integer> list = pembayaranDao.getAllIdKunjungan();
        for (Integer id : list) {
            cmbIdKunjungan.addItem(String.valueOf(id));
        }
    }

    private void tambahListenerAutoHitung() {
        txtTotalTindakan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
        });
 
        txtTotalObat.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungTotalBiaya(); }
        });
 
        txtTotalBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
        });
 
        txtTotalBiaya.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hitungKembalian(); }
        });
        
    }
    
    private void cmbIdKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        cariDataKunjungan();
    }
}
 
    private void hitungTotalBiaya() {
        try {
            String tindakanStr = txtTotalTindakan.getText().trim();
            String obatStr = txtTotalObat.getText().trim();
            BigDecimal tindakan = tindakanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tindakanStr);
            BigDecimal obat = obatStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(obatStr);
            txtTotalBiaya.setText(tindakan.add(obat).toPlainString());
        } catch (NumberFormatException ex) {
            txtTotalBiaya.setText("0");
        }
    }
 
    private void hitungKembalian() {
        try {
            String totalBayarStr = txtTotalBayar.getText().trim();
            String totalBiayaStr = txtTotalBiaya.getText().trim();
 
            BigDecimal totalBayar = totalBayarStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalBayarStr);
            BigDecimal totalBiaya = totalBiayaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalBiayaStr);
 
            BigDecimal kembalian = totalBayar.subtract(totalBiaya);
 
            if (kembalian.compareTo(BigDecimal.ZERO) < 0) {
                txtKembalian.setText("Uang Kurang");
            } else {
                txtKembalian.setText(kembalian.toPlainString());
            }
        } catch (NumberFormatException ex) {
            txtKembalian.setText("0");
        }
    }
 
    private void cariDataKunjungan() {
        try {
        if (cmbIdKunjungan.getSelectedItem() == null) return;
        int idKunjungan = Integer.parseInt(cmbIdKunjungan.getSelectedItem().toString());
        txtNamaPasien.setText(pembayaranDao.getNamaPasienByKunjungan(idKunjungan));
        txtTotalTindakan.setText(pembayaranDao.getTotalTindakanByKunjungan(idKunjungan).toPlainString());
        txtTotalObat.setText(pembayaranDao.getTotalObatByKunjungan(idKunjungan).toPlainString());
        hitungTotalBiaya();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data kunjungan!");
    }
    }
 
    private String buatKodePembayaran() {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        return "PAY-" + timestamp;
    }
 
    public void tampilkanData() {
        DefaultTableModel model = (DefaultTableModel) tblPembayaran.getModel();
        model.setRowCount(0);
 
        List<Object[]> list = pembayaranDao.getAllPembayaranTampil();
        for (Object[] row : list) {
            model.addRow(row);
        }
    }
 
    public void kosongkanForm() {
        txtIdBayar.setText("");
        if (cmbIdKunjungan.getItemCount() > 0) {
        cmbIdKunjungan.setSelectedIndex(-1);
    }
        txtTanggalBayar.setText(java.time.LocalDate.now().toString());
        txtTotalTindakan.setText("0");
        txtTotalObat.setText("0");
        txtTotalBiaya.setText("0");
        txtTotalBayar.setText("");
        txtKembalian.setText("");
        txtKodePembayaran.setText("");
        txtCari.setText("");
        txtNamaPasien.setText("");
        cmbMetodeBayar.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtTotalTindakan = new javax.swing.JTextField();
        txtTotalObat = new javax.swing.JTextField();
        txtIdBayar = new javax.swing.JTextField();
        txtTanggalBayar = new javax.swing.JTextField();
        txtTotalBiaya = new javax.swing.JTextField();
        cmbMetodeBayar = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPembayaran = new javax.swing.JTable();
        lblNamaPasien = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtNamaPasien = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTotalBayar = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        txtKodePembayaran = new javax.swing.JTextField();
        cmbIdKunjungan = new javax.swing.JComboBox<>();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ID Bayar");

        jLabel3.setText("ID Kunjungan");

        jLabel4.setText("Tanggal Bayar");

        jLabel5.setText("Total Tindakan");

        jLabel6.setText("Total Obat");

        jLabel7.setText("Total Biaya");

        jLabel8.setText("Metode Bayar");

        txtTotalTindakan.setEditable(false);
        txtTotalTindakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalTindakanActionPerformed(evt);
            }
        });

        txtTotalObat.setEditable(false);

        txtIdBayar.setEnabled(false);
        txtIdBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdBayarActionPerformed(evt);
            }
        });

        txtTanggalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalBayarActionPerformed(evt);
            }
        });

        txtTotalBiaya.setEditable(false);

        cmbMetodeBayar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tunai", "Transfer", "Qris", "Debit" }));

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

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        tblPembayaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Bayar", "ID Kunjungan", "Tanggal Bayar", "Total Bayar", "Metode Bayar", "Kode Pembayaran"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPembayaran.setGridColor(new java.awt.Color(0, 0, 0));
        tblPembayaran.setShowGrid(true);
        tblPembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPembayaranMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPembayaran);
        if (tblPembayaran.getColumnModel().getColumnCount() > 0) {
            tblPembayaran.getColumnModel().getColumn(0).setHeaderValue("ID Bayar");
            tblPembayaran.getColumnModel().getColumn(1).setHeaderValue("ID Kunjungan");
            tblPembayaran.getColumnModel().getColumn(2).setHeaderValue("Tanggal Bayar");
            tblPembayaran.getColumnModel().getColumn(3).setHeaderValue("Total Bayar");
            tblPembayaran.getColumnModel().getColumn(4).setHeaderValue("Metode Bayar");
            tblPembayaran.getColumnModel().getColumn(5).setHeaderValue("Kode Pembayaran");
        }

        lblNamaPasien.setText("Nama Pasien");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("F O R M U L I R   P E M B A Y A R A N");

        txtNamaPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaPasienActionPerformed(evt);
            }
        });

        jLabel9.setText("Total Bayar");

        txtTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayarActionPerformed(evt);
            }
        });

        jLabel10.setText("Kembalian");

        txtKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKembalianActionPerformed(evt);
            }
        });

        jTextField1.setText("Kode Pembayaran");

        txtKodePembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodePembayaranActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtKodePembayaran)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtIdBayar)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(txtTotalTindakan)
                                            .addComponent(txtTanggalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel3)
                                            .addComponent(txtTotalObat)
                                            .addComponent(lblNamaPasien)
                                            .addComponent(txtNamaPasien, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                            .addComponent(cmbIdKunjungan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(jLabel7)
                                    .addComponent(txtTotalBiaya, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cmbMetodeBayar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 1256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCari, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbIdKunjungan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lblNamaPasien))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTanggalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNamaPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTotalTindakan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(28, 28, 28)))
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalBiaya, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKodePembayaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSimpan)
                            .addComponent(btnHapus)
                            .addComponent(btnBatal))))
                .addContainerGap(225, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTotalTindakanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalTindakanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalTindakanActionPerformed

    private void tblPembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPembayaranMouseClicked
        // TODO add your handling code here:
         int row = tblPembayaran.getSelectedRow();
        if (row < 0) return;
 
        int idBayar = Integer.parseInt(tblPembayaran.getValueAt(row, 0).toString());
        Pembayaran p = pembayaranDao.getById(idBayar);
 
        // metode_bayar dan kode_pembayaran tidak ada di model Pembayaran,
        // jadi diambil langsung dari baris tabel yang sedang ditampilkan.
        Object metodeBayarRow = tblPembayaran.getValueAt(row, 4);
        Object kodePembayaranRow = tblPembayaran.getValueAt(row, 5);
 
        txtIdBayar.setText(String.valueOf(p.getIdBayar()));
        cmbIdKunjungan.setSelectedItem(String.valueOf(p.getIdKunjungan()));        txtTanggalBayar.setText(p.getTglBayar());
        txtTotalTindakan.setText(p.getTotalTindakan() != null ? p.getTotalTindakan().toPlainString() : "0");
        txtTotalObat.setText(p.getTotalObat() != null ? p.getTotalObat().toPlainString() : "0");
        cmbMetodeBayar.setSelectedItem(metodeBayarRow != null ? metodeBayarRow.toString() : "Tunai");
        txtTotalBayar.setText(p.getTotalBayar() != null ? p.getTotalBayar().toPlainString() : "0");
        txtKodePembayaran.setText(kodePembayaranRow != null ? kodePembayaranRow.toString() : "");
 
        hitungTotalBiaya();
 
        txtNamaPasien.setText(pembayaranDao.getNamaPasienByKunjungan(p.getIdKunjungan()));
    }//GEN-LAST:event_tblPembayaranMouseClicked

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
         try {
            if (cmbIdKunjungan.getSelectedItem() == null || txtTotalBayar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data Kunjungan / Total Bayar belum lengkap!");
            return;
            }
 
            if (txtKembalian.getText().equals("Uang Kurang")) {
                JOptionPane.showMessageDialog(this, "Transaksi ditolak: Pembayaran Kurang!");
                return;
            }
 
            if (txtKodePembayaran.getText().trim().isEmpty()) {
                txtKodePembayaran.setText(buatKodePembayaran());
            }
 
            Pembayaran p = new Pembayaran();
            boolean isUpdate = !txtIdBayar.getText().trim().isEmpty();
            if (isUpdate) {
                p.setIdBayar(Integer.parseInt(txtIdBayar.getText().trim()));
            }
 
            p.setIdKunjungan(Integer.parseInt(cmbIdKunjungan.getSelectedItem().toString()));
            p.setTotalTindakan(new BigDecimal(txtTotalTindakan.getText().trim()));
            p.setTotalObat(new BigDecimal(txtTotalObat.getText().trim()));
            p.setTotalBayar(new BigDecimal(txtTotalBayar.getText().trim()));
            p.setTglBayar(isUpdate ? txtTanggalBayar.getText().trim() : java.time.LocalDate.now().toString());
 
            String metodeBayar = cmbMetodeBayar.getSelectedItem().toString();
            String kodePembayaran = txtKodePembayaran.getText().trim();
 
            boolean sukses = isUpdate
                    ? pembayaranDao.updatePembayaran(p, metodeBayar, kodePembayaran)
                    : pembayaranDao.tambahPembayaran(p, metodeBayar, kodePembayaran);
 
            if (sukses) {
                JOptionPane.showMessageDialog(this, isUpdate ? "Data berhasil diperbarui" : "Data berhasil disimpan");
                kosongkanForm();
                tampilkanData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Pastikan ID Kunjungan dan nominal diisi dengan angka yang valid!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        if (txtIdBayar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus dari tabel terlebih dahulu!");
            return;
        }
 
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;
 
        int idBayar = Integer.parseInt(txtIdBayar.getText().trim());
        if (pembayaranDao.hapusPembayaran(idBayar)) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus");
            kosongkanForm();
            tampilkanData();
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblPembayaran.getModel();
        model.setRowCount(0);
 
        List<Object[]> list = pembayaranDao.cariPembayaranTampil(txtCari.getText().trim());
        for (Object[] row : list) {
            model.addRow(row);
        }
    }//GEN-LAST:event_btnCariActionPerformed

    private void txtIdBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdBayarActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        kosongkanForm();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void txtTanggalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalBayarActionPerformed

    private void txtNamaPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaPasienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaPasienActionPerformed

    private void txtTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayarActionPerformed
        // TODO add your handling code here:
                hitungKembalian();
    }//GEN-LAST:event_txtTotalBayarActionPerformed

    private void txtKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKembalianActionPerformed

    private void txtKodePembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodePembayaranActionPerformed
        // TODO add your handling code here:
        txtKodePembayaran.setText(buatKodePembayaran());
    }//GEN-LAST:event_txtKodePembayaranActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
 
        java.awt.EventQueue.invokeLater(() -> new FormPembayaran(0).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<String> cmbIdKunjungan;
    private javax.swing.JComboBox<String> cmbMetodeBayar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblNamaPasien;
    private javax.swing.JTable tblPembayaran;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdBayar;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtKodePembayaran;
    private javax.swing.JTextField txtNamaPasien;
    private javax.swing.JTextField txtTanggalBayar;
    private javax.swing.JTextField txtTotalBayar;
    private javax.swing.JTextField txtTotalBiaya;
    private javax.swing.JTextField txtTotalObat;
    private javax.swing.JTextField txtTotalTindakan;
    // End of variables declaration//GEN-END:variables
}
