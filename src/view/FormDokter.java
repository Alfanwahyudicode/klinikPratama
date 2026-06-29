package view;

import dao.DokterDao;
import model.Dokter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form GUI Resmi untuk Manajemen Data Dokter Terintegrasi penuh dengan
 * DokterDao (Tanpa kolom alamat)
 *
 * @author VanZ
 */
public class FormDokter extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormDokter.class.getName());
    private final DokterDao dokterDao = new DokterDao();

    public FormDokter() {
        initComponents();
        txtIdDokter.setEditable(false); // ID diatur otomatis oleh database (Auto Increment)
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
                return false; // Sel JTable tidak dapat diedit langsung secara manual
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

        btnCari = new javax.swing.JButton();
        txtIdDokter = new javax.swing.JTextField();
        txtNamaDokter = new javax.swing.JTextField();
        cmbSpesialis = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnHapus = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDokter = new javax.swing.JTable();
        btnBatal = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnCari.setText("Cari");

        txtIdDokter.setEditable(false);

        cmbSpesialis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "[-- Pilih --]", "Umum", "Anak", "Penyakit Dalam", "Gigi" }));

        jLabel5.setText("No Telepon");

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

        jLabel1.setText("ID Dokter");

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        tblDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Dokter", "Nama Dokter", "Spesialisasi", "No Telepon"
            }
        ));
        tblDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDokterMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblDokter);

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jLabel3.setText("Spesialis");

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });

        txtTelepon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTeleponActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama Dokter");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUbah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbSpesialis, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtTelepon)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtIdDokter)
                                    .addComponent(txtNamaDokter))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNamaDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbSpesialis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnUbah)
                    .addComponent(btnHapus)
                    .addComponent(btnBatal)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTeleponActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {
        btnCariActionPerformed(evt);
    }

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtIdDokter.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data dokter pada tabel terlebih dahulu!");
            return;
        }

        if (txtNamaDokter.getText().trim().isEmpty() || cmbSpesialis.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Dokter dan Spesialis wajib diisi!");
            return;
        }

        Dokter d = new Dokter();
        // Mengambil ID dari textfield dengan aman
        d.setIdDokter(Integer.parseInt(txtIdDokter.getText().trim()));
        d.setNamaDokter(txtNamaDokter.getText().trim());
        d.setSpesialisasi(cmbSpesialis.getSelectedItem().toString());
        d.setNoTelp(txtTelepon.getText().trim());

        // Eksekusi update
        if (dokterDao.updateDokter(d)) {
            JOptionPane.showMessageDialog(this, "Data Dokter berhasil diperbarui!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {

        if (txtIdDokter.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data pada tabel yang ingin dihapus terlebih dahulu!");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(txtIdDokter.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Dokter tidak valid untuk dihapus!");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data dokter dengan ID: " + id + "?",
                "Konfirmasi Hapus Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {

            if (dokterDao.hapusDokter(id)) {
                JOptionPane.showMessageDialog(this, "Data Dokter berhasil dihapus!");
                tampilkanDataTabel();
                bersihkanForm();
            }
        }
    }

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        bersihkanForm();
        tampilkanDataTabel();
    }

    private void tblDokterMouseClicked(java.awt.event.MouseEvent evt) {
        isiFormDariTabel();
    }

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

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
