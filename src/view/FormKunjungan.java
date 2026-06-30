/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.DokterDao;
import dao.KunjunganDao;
import dao.PasienDao;
import model.Dokter;
import model.Kunjungan;
import model.Pasien;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author VanZ
 */
public class FormKunjungan extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormKunjungan.class.getName());
 
    private final KunjunganDao kunjunganDao = new KunjunganDao();
    private final PasienDao pasienDao = new PasienDao();
    private final DokterDao dokterDao = new DokterDao();
 
    private List<Pasien> listPasien;
    private List<Dokter> listDokter;
    
    private boolean sedangUbah = false;
    /**
     * Creates new form FormKunjungan
     */
    public FormKunjungan() {
        initComponents();
        txtIdKunjungan.setEditable(false);
        txtNamaPasien.setEditable(false);
        txtNamaDokter.setEditable(false);
        txtTanggal.setEditable(false);
 
        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"Daftar", "Selesai", "Batal"}));
 
        muatComboPasien();
        muatComboDokter();
        tampilkanDataTabel();
        formBaru();
 
        tblKunjungan.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });
    }
    
    private String formatIdKunjungan(int id) {
        return String.format("KJ%03d", id);
    }
 
    private int parseIdKunjungan(String teks) {
        String angka = teks == null ? "" : teks.replaceAll("[^0-9]", "");
        return angka.isEmpty() ? 0 : Integer.parseInt(angka);
    }
    
    private void muatComboPasien() {
        listPasien = pasienDao.getAllPasien();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Pasien p : listPasien) {
            model.addElement(p.getIdPasien());
        }
        cmbIdPasien.setModel(model);
        tampilkanNamaPasienTerpilih();
    }
 
    private void muatComboDokter() {
        listDokter = dokterDao.getAllDokter();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Dokter d : listDokter) {
            model.addElement(d.getIdDokter());
        }
        cmbIdDokter.setModel(model);
        tampilkanNamaDokterTerpilih();
    }
 
    private void tampilkanNamaPasienTerpilih() {
        Integer idTerpilih = (Integer) cmbIdPasien.getSelectedItem();
        if (idTerpilih == null || listPasien == null) {
            txtNamaPasien.setText("");
            return;
        }
        for (Pasien p : listPasien) {
            if (p.getIdPasien() == idTerpilih) {
                txtNamaPasien.setText(p.getNamaPasien());
                return;
            }
        }
        txtNamaPasien.setText("");
    }
 
    private void tampilkanNamaDokterTerpilih() {
        Integer idTerpilih = (Integer) cmbIdDokter.getSelectedItem();
        if (idTerpilih == null || listDokter == null) {
            txtNamaDokter.setText("");
            return;
        }
        for (Dokter d : listDokter) {
            if (d.getIdDokter() == idTerpilih) {
                txtNamaDokter.setText(d.getNamaDokter());
                return;
            }
        }
        txtNamaDokter.setText("");
    }
 
    private void formBaru() {
        sedangUbah = false;
        txtIdKunjungan.setText(formatIdKunjungan(kunjunganDao.getNextIdKunjungan()));
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        jlabel.setText("");
        cmbStatus.setSelectedItem("Daftar");
 
        if (cmbIdPasien.getItemCount() > 0) {
            cmbIdPasien.setSelectedIndex(0);
        }
        if (cmbIdDokter.getItemCount() > 0) {
            cmbIdDokter.setSelectedIndex(0);
        }
        tampilkanNamaPasienTerpilih();
        tampilkanNamaDokterTerpilih();
 
        txtCari.setText("");
        tblKunjungan.clearSelection();
    }
 
    private void tampilkanDataTabel() {
        tampilkanKeTabel(kunjunganDao.getAllKunjungan());
    }
 
    private void tampilkanKeTabel(List<Kunjungan> list) {
        String[] kolom = {"ID Kunjungan", "ID Pasien", "Nama Pasien", "Tanggal Kunjungan", "Status Pasien"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        for (Kunjungan k : list) {
            model.addRow(buatBarisTabel(k));
        }
        tblKunjungan.setModel(model);
    }
 
    private Object[] buatBarisTabel(Kunjungan k) {
        return new Object[]{
            formatIdKunjungan(k.getIdKunjungan()),
            k.getIdPasien(),
            k.getNamaPasien(),
            k.getTanggalKunjungan(),
            kapitalisasi(k.getStatus())
        };
    }
 
    private String kapitalisasi(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
 
    private void isiFormDariTabel() {
        int row = tblKunjungan.getSelectedRow();
        if (row < 0) {
            return;
        }
 
        DefaultTableModel model = (DefaultTableModel) tblKunjungan.getModel();
        int idKunjungan = parseIdKunjungan(model.getValueAt(row, 0).toString());
 
        Kunjungan k = kunjunganDao.getKunjunganById(idKunjungan);
        if (k == null) {
            return;
        }
 
        sedangUbah = true;
        txtIdKunjungan.setText(formatIdKunjungan(k.getIdKunjungan()));
        cmbIdPasien.setSelectedItem(k.getIdPasien());
        cmbIdDokter.setSelectedItem(k.getIdDokter());
        tampilkanNamaPasienTerpilih();
        tampilkanNamaDokterTerpilih();
        jlabel.setText(k.getKeluhan());
        txtTanggal.setText(k.getTanggalKunjungan() != null ? k.getTanggalKunjungan().toString() : "");
        cmbStatus.setSelectedItem(kapitalisasi(k.getStatus()));
    }
 
    private boolean validasiForm() {
        if (cmbIdPasien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Belum ada data pasien. Tambahkan data pasien terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cmbIdDokter.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Belum ada data dokter. Tambahkan data dokter terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtKeluhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keluhan pasien tidak boleh kosong!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbDokter = new javax.swing.JLabel();
        cbPasien = new javax.swing.JLabel();
        cmbIdDokter = new javax.swing.JComboBox<>();
        cmbIdPasien = new javax.swing.JComboBox<>();
        jlabel = new javax.swing.JLabel();
        txtKeluhan = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblKunjungan = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNamaPasien = new javax.swing.JTextField();
        txtNamaDokter = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTanggal = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtIdKunjungan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cbDokter.setText("ID Dokter");

        cbPasien.setText("ID Pasien");

        cmbIdDokter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbIdDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIdDokterActionPerformed(evt);
            }
        });

        cmbIdPasien.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbIdPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIdPasienActionPerformed(evt);
            }
        });

        jlabel.setText("Keluhan Pasien");

        txtKeluhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKeluhanActionPerformed(evt);
            }
        });

        tblKunjungan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Kunjungan", "ID Pasien", "Nama Pasien", "Tanggal Kunjungan", "Sratus Pasien"
            }
        ));
        jScrollPane2.setViewportView(tblKunjungan);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("K U N J U N G A N");

        jLabel4.setText("Nama Pasien");

        jLabel5.setText("Nama Dokter");

        txtNamaPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaPasienActionPerformed(evt);
            }
        });

        jLabel6.setText("Tanggal Kunjungan");

        txtTanggal.setText("yyyy/MM/dd");
        txtTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalActionPerformed(evt);
            }
        });

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText("Status Pasien");

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
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

        btnCari.setText("Cari");
        btnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariActionPerformed(evt);
            }
        });

        jLabel8.setText("ID Kunjungan");

        txtIdKunjungan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdKunjunganActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(298, 298, 298))
            .addGroup(layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbIdPasien, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbPasien, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNamaPasien, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbDokter)
                            .addComponent(cmbIdDokter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(txtNamaDokter, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlabel)
                            .addComponent(txtKeluhan, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addGap(268, 268, 268)
                            .addComponent(jLabel7))
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdKunjungan))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)))
                .addGap(32, 32, 32)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdKunjungan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPasien)
                    .addComponent(cbDokter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbIdPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbIdDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNamaPasien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNamaDokter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jlabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKeluhan, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnHapus)
                    .addComponent(btnBatal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtKeluhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKeluhanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKeluhanActionPerformed

    private void txtNamaPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaPasienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaPasienActionPerformed

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
        if (!validasiForm()) {
            return;
        }
 
        Kunjungan k = new Kunjungan();
        k.setIdPasien((Integer) cmbIdPasien.getSelectedItem());
        k.setIdDokter((Integer) cmbIdDokter.getSelectedItem());
        k.setTanggalKunjungan(Date.valueOf(txtTanggal.getText().trim()));
        k.setKeluhan(txtKeluhan.getText().trim());
        k.setStatus(((String) cmbStatus.getSelectedItem()).toLowerCase());
 
        boolean sukses;
        if (sedangUbah) {
            k.setIdKunjungan(parseIdKunjungan(txtIdKunjungan.getText()));
            sukses = kunjunganDao.updateKunjungan(k);
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Data kunjungan berhasil diubah.");
            }
        } else {
            sukses = kunjunganDao.tambahKunjungan(k);
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Data kunjungan berhasil disimpan.");
            }
        }
 
        if (sukses) {
            tampilkanDataTabel();
            formBaru();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data kunjungan.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
                lakukanPencarian();
    }//GEN-LAST:event_txtCariActionPerformed

    private void txtIdKunjunganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdKunjunganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdKunjunganActionPerformed

    private void cmbIdDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdDokterActionPerformed
        // TODO add your handling code here:
        tampilkanNamaDokterTerpilih();
    }//GEN-LAST:event_cmbIdDokterActionPerformed

    private void cmbIdPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdPasienActionPerformed
        // TODO add your handling code here:
        tampilkanNamaPasienTerpilih();
    }//GEN-LAST:event_cmbIdPasienActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        int row = tblKunjungan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data kunjungan pada tabel terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        DefaultTableModel model = (DefaultTableModel) tblKunjungan.getModel();
        int idKunjungan = parseIdKunjungan(model.getValueAt(row, 0).toString());
 
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Hapus data kunjungan " + formatIdKunjungan(idKunjungan) + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
 
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (kunjunganDao.hapusKunjungan(idKunjungan)) {
                JOptionPane.showMessageDialog(this, "Data kunjungan berhasil dihapus.");
                tampilkanDataTabel();
                formBaru();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data kunjungan.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        formBaru();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        // TODO add your handling code here:
        lakukanPencarian();
    }
     private void lakukanPencarian() {
        String keyword = txtCari.getText().trim();
        if (keyword.isEmpty()) {
            tampilkanDataTabel();
        } else {
            tampilkanKeTabel(kunjunganDao.cariKunjungan(keyword));
        }
    }//GEN-LAST:event_btnCariActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new FormKunjungan().setVisible(true));
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
        java.awt.EventQueue.invokeLater(() -> new FormKunjungan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel cbDokter;
    private javax.swing.JLabel cbPasien;
    private javax.swing.JComboBox<String> cmbIdDokter;
    private javax.swing.JComboBox<String> cmbIdPasien;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jlabel;
    private javax.swing.JTable tblKunjungan;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtIdKunjungan;
    private javax.swing.JTextField txtKeluhan;
    private javax.swing.JTextField txtNamaDokter;
    private javax.swing.JTextField txtNamaPasien;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables
}
