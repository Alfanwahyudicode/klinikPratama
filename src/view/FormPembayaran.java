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

    /**
     * Konstruktor Standar (Default)
     */
    public FormPembayaran() {
        initComponents();
        kosongkanForm();
        tampilkanData();
        tambahListenerAutoHitung();
    }

    /**
     * Konstruktor Tambahan untuk Integrasi dengan Form Lain Panggil ini dari
     * form lain dengan: new FormPembayaran(idKunjungan).setVisible(true);
     */
    public FormPembayaran(int idKunjunganEksternal) {
        initComponents();
        kosongkanForm();
        tampilkanData();
        tambahListenerAutoHitung();

        // Mengisi ID Kunjungan otomatis dan memicu pencarian data medisnya
        txtIdKunjungan.setText(String.valueOf(idKunjunganEksternal));
        btnCariKunjunganActionPerformed(null);
    }

    /**
     * Listener Otomatis untuk Semua Field Uang/Biaya
     */
    private void tambahListenerAutoHitung() {
        // Otomatisasi Total Bayar (Tagihan) ketika Tindakan berubah
        txtTotalTindakan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }
        });

        // Otomatisasi Total Bayar (Tagihan) ketika Obat berubah
        txtTotalObat.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungTotalBayar();
            }
        });

        // Otomatisasi Kembalian ketika Total Tagihan (Total Bayar) berubah
        txtTotalBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }
        });

        // Otomatisasi Kembalian ketika Jumlah Uang yang Dibayarkan Pasien (Total Biaya) berubah
        txtTotalBiaya.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hitungKembalian();
            }
        });
    }

    /**
     * Menghitung Total Tagihan (Tindakan + Obat)
     */
    private void hitungTotalBayar() {
        try {
            String tindakanStr = txtTotalTindakan.getText().trim();
            String obatStr = txtTotalObat.getText().trim();
            BigDecimal tindakan = tindakanStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(tindakanStr);
            BigDecimal obat = obatStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(obatStr);
            txtTotalBayar.setText(tindakan.add(obat).toPlainString());
        } catch (NumberFormatException ex) {
            // Abaikan jika input text masih dalam proses pengetikan/belum valid
        }
    }

    /**
     * Menghitung Kembalian secara Otomatis (Uang Masuk - Total Tagihan)
     */
    private void hitungKembalian() {
        try {
            String totalBayarStr = txtTotalBayar.getText().trim();
            String totalBiayaStr = txtTotalBiaya.getText().trim(); // txtTotalBiaya = Uang Tunai dari Pasien

            BigDecimal totalBayar = totalBayarStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalBayarStr);
            BigDecimal totalBiaya = totalBiayaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalBiayaStr);

            BigDecimal kembalian = totalBiaya.subtract(totalBayar);

            if (kembalian.compareTo(BigDecimal.ZERO) < 0) {
                txtKembalian.setText("Uang Kurang");
            } else {
                txtKembalian.setText(kembalian.toPlainString());
            }
        } catch (NumberFormatException ex) {
            txtKembalian.setText("0");
        }
    }

    public void tampilkanData() {
        DefaultTableModel model = (DefaultTableModel) tblPembayaran.getModel();
        model.setRowCount(0);

        List<Pembayaran> list = pembayaranDao.getAllPembayaran();
        for (Pembayaran p : list) {
            model.addRow(new Object[]{
                p.getIdBayar(),
                p.getIdKunjungan(),
                p.getTglBayar(),
                p.getTotalTindakan(),
                p.getTotalObat(),
                p.getTotalBayar(),
                p.getMetodeBayar()
            });
        }
    }

    public void kosongkanForm() {
        txtIdBayar.setText("");
        txtIdKunjungan.setText("");
        txtTotalTindakan.setText("");
        txtTotalObat.setText("");
        txtTotalBayar.setText("");
        txtTotalBiaya.setText("");
        txtKembalian.setText("");
        txtCari.setText("");
        lblNamaPasien.setText("-");
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
        txtTotalBayar = new javax.swing.JTextField();
        cmbMetodeBayar = new javax.swing.JComboBox<>();
        txtIdKunjungan = new javax.swing.JTextField();
        btnCariKunjungan = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnTambahBaru = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPembayaran = new javax.swing.JTable();
        lblNamaPasien = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTotalBiaya = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JTextField();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("ID Bayar");

        jLabel3.setText("ID Kunjungan");

        jLabel4.setText("Tanggal Bayar");

        jLabel5.setText("Total Tindakan");

        jLabel6.setText("Total Obat");

        jLabel7.setText("Total Bayar");

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

        txtTotalBayar.setEditable(false);

        cmbMetodeBayar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tunai", "Transfer", "Qris", "Debit" }));

        btnCariKunjungan.setText("Cari");
        btnCariKunjungan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariKunjunganActionPerformed(evt);
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

        btnTambahBaru.setText("Tambah Baru");
        btnTambahBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahBaruActionPerformed(evt);
            }
        });

        jLabel9.setText("Cari Data");

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        tblPembayaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Bayar", "ID Kunjungan", "Tanggal Bayar", "Total Tindakan", "Total Obat", "Total Bayar", "Metode Bayar"
            }
        ));
        tblPembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPembayaranMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPembayaran);

        lblNamaPasien.setText("Nama Pasien");

        jLabel10.setText("Total Biaya");

        jLabel11.setText("Kembalian");

        txtKembalian.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(cmbMetodeBayar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSimpan)
                            .addGap(18, 18, 18)
                            .addComponent(btnHapus)
                            .addGap(18, 18, 18)
                            .addComponent(btnTambahBaru))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(18, 18, 18)
                            .addComponent(txtCari)
                            .addGap(18, 18, 18)
                            .addComponent(btnCari)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 609, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtIdKunjungan, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCariKunjungan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblNamaPasien))
                            .addComponent(txtTanggalBayar)
                            .addComponent(txtTotalTindakan)
                            .addComponent(txtTotalObat)
                            .addComponent(txtTotalBayar)
                            .addComponent(txtIdBayar)
                            .addComponent(txtTotalBiaya)
                            .addComponent(txtKembalian))))
                .addContainerGap(402, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIdKunjungan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCariKunjungan)
                    .addComponent(lblNamaPasien))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTanggalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtTotalTindakan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addComponent(txtTotalObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtTotalBiaya, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cmbMetodeBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnHapus)
                    .addComponent(btnTambahBaru))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(181, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTotalTindakanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalTindakanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalTindakanActionPerformed

    private void tblPembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPembayaranMouseClicked
        // TODO add your handling code here:
        int row = tblPembayaran.getSelectedRow();
        txtIdBayar.setText(tblPembayaran.getValueAt(row, 0).toString());
        txtIdKunjungan.setText(tblPembayaran.getValueAt(row, 1).toString());
        txtTanggalBayar.setText(tblPembayaran.getValueAt(row, 2).toString());
        txtTotalTindakan.setText(tblPembayaran.getValueAt(row, 3).toString());
        txtTotalObat.setText(tblPembayaran.getValueAt(row, 4).toString());
        txtTotalBayar.setText(tblPembayaran.getValueAt(row, 5).toString());
        cmbMetodeBayar.setSelectedItem(tblPembayaran.getValueAt(row, 6).toString());
        
        // Mengisi nama pasien saat data tabel diklik
        try {
            int idKunjungan = Integer.parseInt(txtIdKunjungan.getText());
            lblNamaPasien.setText(pembayaranDao.getNamaPasienByKunjungan(idKunjungan));
        } catch(Exception e) {
            lblNamaPasien.setText("-");
        }
    }//GEN-LAST:event_tblPembayaranMouseClicked

    private void btnCariKunjunganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariKunjunganActionPerformed
        // TODO add your handling code here:                                              
        try {
            int idKunjungan = Integer.parseInt(txtIdKunjungan.getText());

            String nama = pembayaranDao.getNamaPasienByKunjungan(idKunjungan);
            lblNamaPasien.setText(nama);

            BigDecimal totalTindakan = pembayaranDao.getTotalTindakanByKunjungan(idKunjungan);
            BigDecimal totalObat = pembayaranDao.getTotalObatByKunjungan(idKunjungan);

            // Set text akan memicu DocumentListener menghitung totalBayar & kembalian secara otomatis
            txtTotalTindakan.setText(totalTindakan.toString());
            txtTotalObat.setText(totalObat.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "ID Kunjungan tidak valid atau tidak ditemukan");
        }
    }//GEN-LAST:event_btnCariKunjunganActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        try {
            if(txtIdKunjungan.getText().isEmpty() || txtTotalBayar.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data Kunjungan belum lengkap!");
                return;
            }
            
            if(txtKembalian.getText().equals("Uang Kurang")) {
                JOptionPane.showMessageDialog(this, "Transaksi ditolak: Pembayaran Kurang!");
                return;
            }

            Pembayaran p = new Pembayaran();
            if (!txtIdBayar.getText().isEmpty()) {
                p.setIdBayar(Integer.parseInt(txtIdBayar.getText()));
            }

            p.setIdKunjungan(Integer.parseInt(txtIdKunjungan.getText()));
            p.setTotalTindakan(new BigDecimal(txtTotalTindakan.getText()));
            p.setTotalObat(new BigDecimal(txtTotalObat.getText()));
            p.setTotalBayar(new BigDecimal(txtTotalBayar.getText()));
            p.setMetodeBayar(cmbMetodeBayar.getSelectedItem().toString());
            p.setTglBayar(java.time.LocalDate.now().toString());

            boolean sukses = txtIdBayar.getText().isEmpty() ? 
                             pembayaranDao.tambahPembayaran(p) : pembayaranDao.updatePembayaran(p);

            if (sukses) {
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan");
                kosongkanForm();
                tampilkanData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        if (txtIdBayar.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus dari tabel terlebih dahulu!");
            return;
        }

        int idBayar = Integer.parseInt(txtIdBayar.getText());
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

        List<Pembayaran> list = pembayaranDao.cariPembayaran(txtCari.getText());
        for (Pembayaran p : list) {
            model.addRow(new Object[]{
                p.getIdBayar(),
                p.getIdKunjungan(),
                p.getTglBayar(),
                p.getTotalTindakan(),
                p.getTotalObat(),
                p.getTotalBayar(),
                p.getMetodeBayar()
            });
        }
    }//GEN-LAST:event_btnCariActionPerformed

    private void txtIdBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdBayarActionPerformed

    private void btnTambahBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahBaruActionPerformed
        // TODO add your handling code here:
        kosongkanForm();
    }//GEN-LAST:event_btnTambahBaruActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new FormPembayaran().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCariKunjungan;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahBaru;
    private javax.swing.JComboBox<String> cmbMetodeBayar;
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
    private javax.swing.JLabel lblNamaPasien;
    private javax.swing.JTable tblPembayaran;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdBayar;
    private javax.swing.JTextField txtIdKunjungan;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtTanggalBayar;
    private javax.swing.JTextField txtTotalBayar;
    private javax.swing.JTextField txtTotalBiaya;
    private javax.swing.JTextField txtTotalObat;
    private javax.swing.JTextField txtTotalTindakan;
    // End of variables declaration//GEN-END:variables
}
