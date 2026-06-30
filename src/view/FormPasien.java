package view;

import dao.PasienDao;
import model.Pasien;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Pasien - Akurasi Kolom Database 100%.
 * @author VanZ
 */
public class FormPasien extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPasien.class.getName());
    private final PasienDao pasienDao = new PasienDao();

    public FormPasien() {
        initComponents();
        txtIdPasien.setEditable(false);
        tampilkanDataTabel();
    }

    private void bersihkanForm() {
        txtIdPasien.setText("");
        txtNamaPasien.setText("");
        cmbJenisKelamin.setSelectedIndex(0);
        txtTelepon.setText("");
        txtAlamat.setText("");
        txtCari.setText("");
        tblPasien.clearSelection();
    }

    private void tampilkanDataTabel() {
        String[] kolom = {"No", "Kode", "Nama Pasien", "JK (L/P)", "No Telp", "Alamat"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Pasien> list = pasienDao.getAllPasien();
        int no = 1;
        for (Pasien p : list) {
            // Konversi nilai 'L' atau 'P' dari database agar user friendly di tabel
            String jkFull = (p.getJk() != null && p.getJk().equalsIgnoreCase("L")) ? "Laki-laki" : "Perempuan";
            
            model.addRow(new Object[]{
                no++,
                p.getIdPasien(),
                p.getNamaPasien(),
                jkFull,
                p.getNoTelp(),
                p.getAlamat()
            });
        }
        tblPasien.setModel(model);
    }

    private void isiFormDariTabel() {
        int row = tblPasien.getSelectedRow();
        if (row < 0) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblPasien.getModel();
        txtIdPasien.setText(model.getValueAt(row, 1).toString());
        txtNamaPasien.setText(model.getValueAt(row, 2).toString());
        
        String jkValue = model.getValueAt(row, 3).toString();
        cmbJenisKelamin.setSelectedItem(jkValue);
        
        txtTelepon.setText(model.getValueAt(row, 4).toString());
        txtAlamat.setText(model.getValueAt(row, 5).toString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNamaPasien = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        txtAlamat = new javax.swing.JTextField();
        cmbJenisKelamin = new javax.swing.JComboBox<>();
        btnSimpan = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPasien = new javax.swing.JTable();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        txtIdPasien = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Data Pasien");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Form Pasien");

        jLabel2.setText("Nama Pasien");

        jLabel3.setText("Jenis Kelamin (jk)");

        jLabel4.setText("No Telepon");

        jLabel5.setText("Alamat");

        cmbJenisKelamin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Pilih --", "Laki-laki", "Perempuan" }));

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

        tblPasien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "No", "Kode", "Nama Pasien", "JK", "No Telp", "Alamat"
            }
        ));
        tblPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPasienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPasien);

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
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
                            .addComponent(txtNamaPasien)
                            .addComponent(txtTelepon)
                            .addComponent(txtAlamat)
                            .addComponent(cmbJenisKelamin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtIdPasien, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(txtIdPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNamaPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbJenisKelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus)
                    .addComponent(btnBatal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:txtCariActionPerformed
        btnCariActionPerformed(evt);
    }//GEN-LAST:txtCariActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnSimpanActionPerformed
        if (txtNamaPasien.getText().trim().isEmpty() || cmbJenisKelamin.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Pasien dan Jenis Kelamin wajib diisi!");
            return;
        }

        Pasien p = new Pasien();
        p.setNamaPasien(txtNamaPasien.getText().trim());
        
        // Konversi item ComboBox menjadi 'L' atau 'P' sesuai ENUM database kolom jk
        String jkDb = cmbJenisKelamin.getSelectedItem().toString().equals("Laki-laki") ? "L" : "P";
        p.setJk(jkDb);
        
        p.setNoTelp(txtTelepon.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        p.setNoRm("RM-" + System.currentTimeMillis() / 100000); // Penanganan otomatis kolom no_rm agar tidak kosong

        if (pasienDao.tambahPasien(p)) {
            JOptionPane.showMessageDialog(this, "Data Pasien berhasil disimpan!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }//GEN-LAST:btnSimpanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnUbahActionPerformed
        if (txtIdPasien.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pills data pasien pada tabel terlebih dahulu!");
            return;
        }

        if (txtNamaPasien.getText().trim().isEmpty() || cmbJenisKelamin.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Pasien dan Jenis Kelamin wajib diisi!");
            return;
        }

        Pasien p = new Pasien();
        p.setIdPasien(Integer.parseInt(txtIdPasien.getText().trim()));
        p.setNamaPasien(txtNamaPasien.getText().trim());
        
        String jkDb = cmbJenisKelamin.getSelectedItem().toString().equals("Laki-laki") ? "L" : "P";
        p.setJk(jkDb);
        
        p.setNoTelp(txtTelepon.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());

        if (pasienDao.updatePasien(p)) {
            JOptionPane.showMessageDialog(this, "Data Pasien berhasil diperbarui!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }//GEN-LAST:btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnHapusActionPerformed
        if (txtIdPasien.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pasien pada tabel terlebih dahulu!");
            return;
        }

        int id = Integer.parseInt(txtIdPasien.getText().trim());
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus data pasien dengan ID " + id + "?", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (pasienDao.hapusPasien(id)) {
                JOptionPane.showMessageDialog(this, "Data Pasien berhasil dihapus!");
                tampilkanDataTabel();
                bersihkanForm();
            }
        }
    }//GEN-LAST:btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnBatalActionPerformed
        bersihkanForm();
        tampilkanDataTabel();
    }//GEN-LAST:btnBatalActionPerformed

    private void tblPasienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:tblPasienMouseClicked
        isiFormDariTabel();
    }//GEN-LAST:tblPasienMouseClicked

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnCariActionPerformed
        String keyword = txtCari.getText().trim();
        String[] kolom = {"No", "Kode", "Nama Pasien", "JK (L/P)", "No Telp", "Alamat"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Pasien> list = pasienDao.cariPasien(keyword);
        int no = 1;
        for (Pasien p : list) {
            String jkFull = (p.getJk() != null && p.getJk().equalsIgnoreCase("L")) ? "Laki-laki" : "Perempuan";
            model.addRow(new Object[]{
                no++,
                p.getIdPasien(),
                p.getNamaPasien(),
                jkFull,
                p.getNoTelp(),
                p.getAlamat()
            });
        }
        tblPasien.setModel(model);
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

        java.awt.EventQueue.invokeLater(() -> new FormPasien().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbJenisKelamin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPasien;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdPasien;
    private javax.swing.JTextField txtNamaPasien;
    private javax.swing.JTextField txtTelepon;
    // End of variables declaration//GEN-END:variables
}