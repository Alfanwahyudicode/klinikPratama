package view;

import dao.PasienDao;
import model.Pasien;
import java.sql.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * @author VanZ
 */
public class FormPasien extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPasien.class.getName());
    private final PasienDao pasienDao = new PasienDao();

    // ====== Agar ID Pasien & Nama Pasien yang sedang dipilih bisa diakses dari kelas lain ======
    // Contoh pemakaian di kelas lain: FormPasien.getIdPasienTerpilih(), FormPasien.getNamaPasienTerpilih()
    private static int idPasienTerpilih = -1;
    private static String namaPasienTerpilih = "";

    public static int getIdPasienTerpilih() {
        return idPasienTerpilih;
    }

    public static String getNamaPasienTerpilih() {
        return namaPasienTerpilih;
    }

    private static void setPasienTerpilih(int id, String nama) {
        idPasienTerpilih = id;
        namaPasienTerpilih = nama;
    }

    public FormPasien() {
        initComponents();
        txtIdPasien.setEditable(false);
        tampilkanDataTabel();
    }

    private void bersihkanForm() {
        txtIdPasien.setText("");
        txtNamaPasien.setText("");
        cmbJenisKelamin.setSelectedIndex(0);
        txtTglLahir.setText("yyyy-MM-dd");
        txtTelepon.setText("");
        txtAlamat.setText("");
        txtCari.setText("");
        tblPasien.clearSelection();
    }

      private void tampilkanDataTabel() {
        String[] kolom = {"ID Pasien", "Nama Pasien", "Jenis Kemlamin", "Tanggal Lahir", "Alamat", "No. Telepon"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Pasien> list = pasienDao.getAllPasien();
        for (Pasien p : list) {
            model.addRow(buatBarisTabel(p));
        }
        tblPasien.setModel(model);
    }

    private Object[] buatBarisTabel(Pasien p) {
        String jkFull = (p.getJk() != null && p.getJk().equalsIgnoreCase("L")) ? "Laki-laki" : "Perempuan";
        return new Object[]{
            p.getIdPasien(),
            p.getNamaPasien(),
            jkFull,
            p.getTglLahir(),
            p.getAlamat(),
            p.getNoTelp()
        };
    }

    private void isiFormDariTabel() {
        int row = tblPasien.getSelectedRow();
        if (row < 0) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblPasien.getModel();

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String nama = model.getValueAt(row, 1).toString();

        txtIdPasien.setText(String.valueOf(id));
        txtNamaPasien.setText(nama);

        String jkValue = model.getValueAt(row, 2).toString();
        cmbJenisKelamin.setSelectedItem(jkValue);

        Object tgl = model.getValueAt(row, 3);
        txtTglLahir.setText(tgl != null ? tgl.toString() : "yyyy-MM-dd");

        txtAlamat.setText(model.getValueAt(row, 4).toString());
        txtTelepon.setText(model.getValueAt(row, 5).toString());

        // Simpan agar bisa diakses oleh kelas lain (mis. FormKunjungan, FormResep, dll.)
        setPasienTerpilih(id, nama);
    }

    private boolean validasiTglLahir() {
        String tgl = txtTglLahir.getText().trim();
        if (tgl.isEmpty() || tgl.equalsIgnoreCase("yyyy-MM-dd")) {
            return true; // boleh dikosongkan
        }
        try {
            Date.valueOf(tgl);
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Format Tanggal Lahir salah! Gunakan: yyyy-MM-dd",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdPasien = new javax.swing.JTextField();
        txtNamaPasien = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmbJenisKelamin = new javax.swing.JComboBox();
        txtTglLahir = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtTelepon = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPasien = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("D A T A   P A S I E N");

        jLabel2.setText("ID Pasien");

        jLabel3.setText("Nama Pasien");

        jLabel4.setText("Jenis Kelamin");

        jLabel5.setText("Tanggal Lahir");

        cmbJenisKelamin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Pilih --", "Laki-laki", "Perempuan" }));

        txtTglLahir.setText("yyyy-MM-dd");

        jLabel6.setText("Alamat");

        jLabel7.setText("Nomor Telepon");

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

        txtCari.setText("Cari Pasien.....");
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

        tblPasien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Pasien", "Nama Pasien", "Jenis Kelamin", "Tanggal Lahir", "Alamat", "No. Telepon"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPasien.getTableHeader().setReorderingAllowed(false);
        tblPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPasienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPasien);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIdPasien)
                            .addComponent(cmbJenisKelamin, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtNamaPasien)
                            .addComponent(txtTglLahir)
                            .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtCari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNamaPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJenisKelamin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTglLahir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan)
                    .addComponent(btnHapus)
                    .addComponent(btnBatal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:txtCariActionPerformed
        btnCariActionPerformed(evt);
    }//GEN-LAST:txtCariActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnSimpanActionPerformed
        if (txtNamaPasien.getText().trim().isEmpty() || cmbJenisKelamin.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Nama Pasien dan Jenis Kelamin wajib diisi!");
            return;
        }
        if (!validasiTglLahir()) return;

        Pasien p = new Pasien();
        p.setNamaPasien(txtNamaPasien.getText().trim());

        String jkDb = cmbJenisKelamin.getSelectedItem().toString().equals("Laki-laki") ? "L" : "P";
        p.setJk(jkDb);

        String tgl = txtTglLahir.getText().trim();
        p.setTglLahir((tgl.isEmpty() || tgl.equalsIgnoreCase("yyyy-MM-dd")) ? null : tgl);

        p.setNoTelp(txtTelepon.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        p.setNoRm("RM-" + System.currentTimeMillis() / 100000); // Penanganan otomatis kolom no_rm agar tidak kosong

        if (pasienDao.tambahPasien(p)) {
            JOptionPane.showMessageDialog(this, "Data Pasien berhasil disimpan!");
            tampilkanDataTabel();
            bersihkanForm();
        }
    }//GEN-LAST:btnSimpanActionPerformed

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
                if (idPasienTerpilih == id) {
                    setPasienTerpilih(-1, "");
                }
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
        if (keyword.isEmpty() || keyword.equalsIgnoreCase("Cari Pasien.....")) {
            tampilkanDataTabel();
            return;
        }

        String[] kolom = {"ID Pasien", "Nama Pasien", "Jenis Kemlamin", "Tanggal Lahir", "Alamat", "No. Telepon"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Pasien> list = pasienDao.cariPasien(keyword);
        for (Pasien p : list) {
            model.addRow(buatBarisTabel(p));
        }
        tblPasien.setModel(model);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Info",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:btnCariActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JComboBox cmbJenisKelamin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPasien;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdPasien;
    private javax.swing.JTextField txtNamaPasien;
    private javax.swing.JTextField txtTelepon;
    private javax.swing.JTextField txtTglLahir;
    // End of variables declaration//GEN-END:variables
}
