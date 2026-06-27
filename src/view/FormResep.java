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
        jTextField2.setText(LocalDate.now().toString());
        txtIdResep.setEditable(false);
        txtSubtotal.setEditable(false);
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
                // tampilkan: "[1] Paracetamol (Tablet)"
                cmbIdObat.addItem(o.toString());
            }
        }
        // langsung isi harga satuan untuk item pertama
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
            txtHargaSatuan.setText(harga != null ? harga.toPlainString() : "0");
            hitungSubtotal();
        }
    }
 
    private void hitungSubtotal() {
        try {
            int jumlah       = Integer.parseInt(txtJumlah.getText().trim());
            BigDecimal harga = new BigDecimal(txtHargaSatuan.getText().trim());
            BigDecimal sub   = harga.multiply(BigDecimal.valueOf(jumlah));
            txtSubtotal.setText(sub.toPlainString());
        } catch (NumberFormatException e) {
            txtSubtotal.setText("0");
        }
    }
    
    private void loadTabelDetail() {
        if (resepAktif == null) return;
 
        listDetail = detailDao.getByResepId(resepAktif.getIdResep());
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
 
        int no = 1;
        for (ResepDetail rd : listDetail) {
            // Cari nama obat dari listObat supaya tabel lebih informatif
            String namaObat = cariNamaObat(rd.getIdObat());
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
 
            // Pilih obat yang sesuai di combo
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
                    ? detailTerpilih.getHargaSatuan().toPlainString() : "0");
            txtSubtotal.setText(detailTerpilih.getSubtotal() != null
                    ? detailTerpilih.getSubtotal().toPlainString() : "0");
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
 
        String tglStr = jTextField2.getText().trim();
        Date tglResep;
        try {
            tglResep = Date.valueOf(tglStr); // format yyyy-MM-dd
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Format tanggal salah! Gunakan: yyyy-MM-dd", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        Resep r = new Resep(pe.getIdPemeriksaan(), tglResep);
        int idBaru = resepDao.tambahResep(r);
 
        if (idBaru > 0) {
            resepAktif = r; // simpan ke state
            txtIdResep.setText(String.valueOf(idBaru)); // tampilkan ID Resep hasil generate DB
            JOptionPane.showMessageDialog(this,
                "Resep berhasil disimpan! ID Resep: " + idBaru + "\nSekarang tambahkan obat.",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdResep = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
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
        btnTambahDetail = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtCari = new javax.swing.JTextField();
        cmbCari = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Resep Obat");

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

        jTextField2.setText("yyyy-MM-dd");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
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

        btnTambahDetail.setText("Tambah");
        btnTambahDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahDetailActionPerformed(evt);
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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "ID Resep", "ID Obat", "Jumlah", "Aturan Pakai", "Harga", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });

        cmbCari.setText("Cari");
        cmbCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtSubtotal)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtAturanPakai)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIdResep)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbIdObat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jSeparator1)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                                    .addComponent(txtJumlah)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnTambahDetail)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnUbah)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnHapus)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnBatal)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtCari)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbCari))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdResep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbIdObat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAturanPakai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambahDetail)
                    .addComponent(btnUbah)
                    .addComponent(btnBatal)
                    .addComponent(btnHapus)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCari))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void btnTambahDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahDetailActionPerformed
        // TODO add your handling code here:
        if (resepAktif == null) {
            simpanResepHeader();
            if (resepAktif == null) return; // simpan header gagal
        }
 
        if (!validasiDetail()) return;
 
        Obat obatDipilih = getObatTerpilih();
        if (obatDipilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih obat terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Cek stok obat mencukupi
        int jumlah = Integer.parseInt(txtJumlah.getText().trim());
        if (jumlah > obatDipilih.getStok()) {
            JOptionPane.showMessageDialog(this,
                "Stok obat tidak cukup! Stok tersedia: " + obatDipilih.getStok(),
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        BigDecimal harga    = new BigDecimal(txtHargaSatuan.getText().trim());
        BigDecimal subtotal = harga.multiply(BigDecimal.valueOf(jumlah));
 
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
            // Kurangi stok obat di DB
            obatDao.updateStok(obatDipilih.getIdObat(), obatDipilih.getStok() - jumlah);
            loadComboObat();       // refresh combo agar stok ter-update
            loadTabelDetail();     // refresh tabel
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Obat berhasil ditambahkan ke resep!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahDetailActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        // TODO add your handling code here:
        if (detailTerpilih == null) {
            JOptionPane.showMessageDialog(this, "Pilih baris pada tabel terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validasiDetail()) return;
 
        Obat obatDipilih = getObatTerpilih();
        if (obatDipilih == null) return;
 
        int jumlahBaru     = Integer.parseInt(txtJumlah.getText().trim());
        int jumlahLama     = detailTerpilih.getJumlah();
        int selisih        = jumlahBaru - jumlahLama;
 
        // Cek stok untuk selisih tambahan
        if (selisih > 0 && selisih > obatDipilih.getStok()) {
            JOptionPane.showMessageDialog(this,
                "Stok obat tidak cukup untuk penambahan jumlah! Stok tersedia: " + obatDipilih.getStok(),
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        BigDecimal harga    = new BigDecimal(txtHargaSatuan.getText().trim());
        BigDecimal subtotal = harga.multiply(BigDecimal.valueOf(jumlahBaru));
 
        detailTerpilih.setIdObat(obatDipilih.getIdObat());
        detailTerpilih.setJumlah(jumlahBaru);
        detailTerpilih.setAturanPakai(txtAturanPakai.getText().trim());
        detailTerpilih.setHargaSatuan(harga);
        detailTerpilih.setSubtotal(subtotal);
 
        if (detailDao.updateDetail(detailTerpilih)) {
            // Sesuaikan stok: kembalikan stok lama, kurangi stok baru
            int stokSekarang = obatDipilih.getStok();
            obatDao.updateStok(obatDipilih.getIdObat(), stokSekarang - selisih);
            loadComboObat();
            loadTabelDetail();
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Data detail resep berhasil diubah!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        Pemeriksaan pe = getPemeriksaanTerpilih();
        if (pe == null) return;
 
        // Cek apakah pemeriksaan ini sudah punya resep
        Resep existing = resepDao.getByIdPemeriksaan(pe.getIdPemeriksaan());
        if (existing != null) {
            // Sudah ada → tampilkan resep yang ada, load detailnya
            resepAktif = existing;
            txtIdResep.setText(String.valueOf(existing.getIdResep()));
            jTextField2.setText(existing.getTglResep() != null
                ? existing.getTglResep().toString() : LocalDate.now().toString());
            loadTabelDetail();
            JOptionPane.showMessageDialog(this,
                "Pemeriksaan ini sudah memiliki resep (ID: " + existing.getIdResep() + ").\nDetail obat ditampilkan di tabel.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Belum ada → reset agar bisa buat resep baru
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
        jTextField2.setText(LocalDate.now().toString());
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Form berhasil direset.",
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnBatalActionPerformed

    private void cmbCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCariActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    private void cmbIdObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdObatActionPerformed
        // TODO add your handling code here:
        isiHargaDariObatTerpilih();
    }//GEN-LAST:event_cmbIdObatActionPerformed

    private void txtHargaSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaSatuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaSatuanActionPerformed
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
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnTambahDetail;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton cmbCari;
    private javax.swing.JComboBox<String> cmbIdObat;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField txtAturanPakai;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHargaSatuan;
    private javax.swing.JTextField txtIdResep;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtSubtotal;
    // End of variables declaration//GEN-END:variables
}
