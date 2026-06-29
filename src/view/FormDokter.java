package view;

import dao.DokterDao;
import model.Dokter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Resmi Terintegrasi Penuh untuk Manajemen Data Dokter.
 * @author VanZ
 */
public class FormDokter extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormDokter.class.getName());
    private final DokterDao dokterDao = new DokterDao();

    public FormDokter() {
        initComponents();
        txtIdDokter.setEditable(false); // ID Auto-increment (tidak boleh diedit manual)
        tampilkanDataTabel();
    }

    private void bersihkanForm() {
        txtIdDokter.setText("");
        txtNamaDokter.setText("");
        cmbSpesialis.setSelectedIndex(0);
        txtTelepon.setText("");
        txtCari.setText("");
        tblDokter.clearSelection();
    }

    private void tampilkanDataTabel() {
        String[] kolom = {"No", "Kode", "Nama Dokter", "Spesialis", "No Telp"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Sel tabel tidak dapat diedit manual lewat ketikan langsung
            }
        };

        List<Dokter> list = dokterDao.getAllDokter();
        int no = 1;
        for (Dokter d : list) {
            model.addRow(new Object[]{
                no++,
                d.getIdDokter(),
                d.getNamaDokter(),
                d.getSpesialisasi(),
                d.getNoTelp()
            });
        }
        tblDokter.setModel(model);
    }

    private void isiFormDariTabel() {
        int row = tblDokter.getSelectedRow();
        if (row < 0) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblDokter.getModel();
        txtIdDokter.setText(model.getValueAt(row, 1).toString());
        txtNamaDokter.setText(model.getValueAt(row, 2).toString());
        cmbSpesialis.setSelectedItem(model.getValueAt(row, 3).toString());
        txtTelepon.setText(model.getValueAt(row, 4).toString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNamaDokter = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        cmbSpesialis = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDokter = new javax.swing.JTable();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        txtIdDokter = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Data Dokter");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Form Dokter");

        jLabel2.setText("Nama Dokter");

        jLabel3.setText("Spesialis");

        jLabel5.setText("No Telepon");

        txtIdDokter.setToolTipText("ID Dokter");

        cmbSpesialis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih Spesialis --", "Umum", "Gigi", "Anak", "Kandungan", "Penyakit Dalam" }));

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
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

        tblDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "No", "Kode", "Nama Dokter", "Spesialis", "No Telp"
            }
        ));
        tblDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDokterMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblDokter);

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSimpan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUbah)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHapus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBatal)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtNamaDokter)
                            .addComponent(txtTelepon)
                            .addComponent(cmbSpesialis, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtIdDokter, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNamaDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbSpesialis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus)
                    .addComponent(btnBatal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:txtCariActionPerformed
        btnCariActionPerformed(evt);
    }//GEN-LAST:txtCariActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnSimpanActionPerformed
        if (txtNamaDokter.getText().trim().isEmpty() || cmbSpesialis.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Dokter dan Spesialis wajib diisi!");
            return;
        }

        Dokter d = new Dokter();
        d.setNamaDokter(txtNamaDokter.getText().trim());
        d.setSpesialisasi(cmbSpesialis.getSelectedItem().toString());
        d.setNoTelp(txtTelepon.getText().trim());

        if (dokterDao.tambahDokter(d)) {
            JOptionPane.showMessageDialog(this, "Data Dokter berhasil disimpan!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }//GEN-LAST:btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnUbahActionPerformed
        if (txtIdDokter.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin diubah terlebih dahulu!");
            return;
        }

        if (txtNamaDokter.getText().trim().isEmpty() || cmbSpesialis.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Dokter dan Spesialis wajib diisi!");
            return;
        }

        Dokter d = new Dokter();
        d.setIdDokter(Integer.parseInt(txtIdDokter.getText().trim()));
        d.setNamaDokter(txtNamaDokter.getText().trim());
        d.setSpesialisasi(cmbSpesialis.getSelectedItem().toString());
        d.setNoTelp(txtTelepon.getText().trim());

        if (dokterDao.updateDokter(d)) {
            JOptionPane.showMessageDialog(this, "Data Dokter berhasil diperbarui!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }//GEN-LAST:btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnHapusActionPerformed
        if (txtIdDokter.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin dihapus terlebih dahulu!");
            return;
        }

        int id = Integer.parseInt(txtIdDokter.getText().trim());
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data dokter dengan ID " + id + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (dokterDao.hapusDokter(id)) {
                JOptionPane.showMessageDialog(this, "Data Dokter berhasil dihapus!");
                tampilkanDataTabel();
                bersihkanForm();
            }
        }
    }//GEN-LAST:btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnBatalActionPerformed
        bersihkanForm();
        tampilkanDataTabel();
    }//GEN-LAST:btnBatalActionPerformed

    private void tblDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:tblDokterMouseClicked
        isiFormDariTabel();
    }//GEN-LAST:tblDokterMouseClicked

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnCariActionPerformed
        String keyword = txtCari.getText().trim();
        String[] kolom = {"No", "Kode", "Nama Dokter", "Spesialis", "No Telp"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Dokter> list = dokterDao.cariDokter(keyword);
        int no = 1;
        for (Dokter d : list) {
            model.addRow(new Object[]{
                no++,
                d.getIdDokter(),
                d.getNamaDokter(),
                d.getSpesialisasi(),
                d.getNoTelp()
            });
        }
        tblDokter.setModel(model);
    }//GEN-LAST:btnCariActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new FormDokter().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbSpesialis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblDokter;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdDokter;
    private javax.swing.JTextField txtNamaDokter;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}