/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
 
import dao.PasienDao;
import model.Pasien;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
 
/**
 * @author VanZ
 */
public class FormPasien extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPasien.class.getName());
    private static final String PLACEHOLDER_TGL = "yyyy-MM-dd";
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
 
    // ====== State mode form: tambah data baru ATAU ubah data yang sudah ada ======
    private boolean modeUbah = false;
    private int idPasienSedangDiubah = -1;
 
    // ====== Komponen kalender (dibuat manual, tanpa library tambahan) ======
    private JPopupMenu popupKalender;
    private JLabel lblBulanTahun;
    private JPanel panelHariGrid;
    private Calendar kalenderAktif = Calendar.getInstance();
 
    public FormPasien() {
        initComponents();
 
        txtIdPasien.setEditable(false);
        txtTglLahir.setEditable(false); // tanggal hanya bisa diisi lewat kalender
        txtTglLahir.setToolTipText("Klik untuk memilih tanggal dari kalender");
        txtTglLahir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                tampilkanKalender();
            }
        });
 
        // No. Telepon hanya boleh diisi angka (huruf/simbol otomatis ditolak saat mengetik/paste)
        pasangFilterAngkaSaja(txtTelepon, 15); // maksimal 15 digit, cukup untuk semua format no HP
 
        // Selain klik langsung di tabel, dukung juga navigasi keyboard/panah
        tblPasien.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });
 
        tampilkanDataTabel();
        formBaru();
    }
 
    /**
     * Reset form ke kondisi "input data baru": generate ID Pasien baru
     * (sekali pakai) dan kosongkan field lain.
     */
    private void formBaru() {
        modeUbah = false;
        idPasienSedangDiubah = -1;
 
        int idBaru = pasienDao.generateIdPasien();
        txtIdPasien.setText(idBaru > 0 ? String.valueOf(idBaru) : "");
 
        txtNamaPasien.setText("");
        cmbJenisKelamin.setSelectedIndex(0);
        txtTglLahir.setText(PLACEHOLDER_TGL);
        txtTelepon.setText("");
        txtAlamat.setText("");
 
        tblPasien.clearSelection();
        btnSimpan.setText("Simpan");
    }
 
    private void bersihkanForm() {
        formBaru();
        txtCari.setText("");
    }
 
    private void tampilkanDataTabel() {
        String[] kolom = {"ID Pasien", "Nama Pasien", "Jenis Kelamin", "Tanggal Lahir", "Alamat", "No. Telepon"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // data pasien di tabel tidak boleh diedit langsung
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
 
        modeUbah = true;
        idPasienSedangDiubah = id;
 
        txtIdPasien.setText(String.valueOf(id));
        txtNamaPasien.setText(nama);
 
        String jkValue = model.getValueAt(row, 2).toString();
        cmbJenisKelamin.setSelectedItem(jkValue);
 
        Object tgl = model.getValueAt(row, 3);
        txtTglLahir.setText(tgl != null ? tgl.toString() : PLACEHOLDER_TGL);
 
        txtAlamat.setText(model.getValueAt(row, 4).toString());
        txtTelepon.setText(model.getValueAt(row, 5).toString());
 
        btnSimpan.setText("Update");
 
        // Simpan agar bisa diakses oleh kelas lain (mis. FormKunjungan, FormResep, dll.)
        setPasienTerpilih(id, nama);
    }
 
    private boolean validasiTglLahir() {
        String tgl = txtTglLahir.getText().trim();
        if (tgl.isEmpty() || tgl.equalsIgnoreCase(PLACEHOLDER_TGL)) {
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
 
    /**
     * Memasang filter pada sebuah JTextField agar hanya menerima karakter
     * angka (0-9), baik saat diketik langsung maupun saat paste/tempel.
     * Huruf dan simbol akan otomatis ditolak (tidak tampil sama sekali).
     *
     * @param field      JTextField yang ingin dibatasi
     * @param maxDigit   jumlah digit maksimal yang boleh diisi
     */
    private void pasangFilterAngkaSaja(javax.swing.JTextField field, int maxDigit) {
        ((PlainDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String hasilSaring = saringAngka(string);
                if (hasilSaring.isEmpty()) {
                    return;
                }
                if (fb.getDocument().getLength() + hasilSaring.length() > maxDigit) {
                    return;
                }
                super.insertString(fb, offset, hasilSaring, attr);
            }
 
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String hasilSaring = saringAngka(text);
                int panjangAkhir = fb.getDocument().getLength() - length + hasilSaring.length();
                if (panjangAkhir > maxDigit) {
                    return;
                }
                super.replace(fb, offset, length, hasilSaring, attrs);
            }
 
            private String saringAngka(String input) {
                if (input == null) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                for (char c : input.toCharArray()) {
                    if (Character.isDigit(c)) {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }
        });
    }
 
    // ====================================================================
    // ================== KALENDER (date picker) manual ==================
    // ====================================================================
 
    private void tampilkanKalender() {
        if (popupKalender == null) {
            buatPanelKalender();
        }
 
        String tglSekarang = txtTglLahir.getText().trim();
        try {
            if (!tglSekarang.isEmpty() && !tglSekarang.equalsIgnoreCase(PLACEHOLDER_TGL)) {
                kalenderAktif.setTime(Date.valueOf(tglSekarang));
            } else {
                kalenderAktif = Calendar.getInstance();
            }
        } catch (IllegalArgumentException e) {
            kalenderAktif = Calendar.getInstance();
        }
 
        perbaruiGridKalender();
        popupKalender.show(txtTglLahir, 0, txtTglLahir.getHeight());
    }
 
    private void buatPanelKalender() {
        popupKalender = new JPopupMenu();
 
        JPanel panelUtama = new JPanel(new BorderLayout(4, 4));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
 
        JPanel panelHeader = new JPanel(new BorderLayout());
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        lblBulanTahun = new JLabel("", SwingConstants.CENTER);
 
        btnPrev.addActionListener(e -> {
            kalenderAktif.add(Calendar.MONTH, -1);
            perbaruiGridKalender();
        });
        btnNext.addActionListener(e -> {
            kalenderAktif.add(Calendar.MONTH, 1);
            perbaruiGridKalender();
        });
 
        panelHeader.add(btnPrev, BorderLayout.WEST);
        panelHeader.add(lblBulanTahun, BorderLayout.CENTER);
        panelHeader.add(btnNext, BorderLayout.EAST);
 
        panelHariGrid = new JPanel(new GridLayout(0, 7, 2, 2));
 
        panelUtama.add(panelHeader, BorderLayout.NORTH);
        panelUtama.add(panelHariGrid, BorderLayout.CENTER);
        popupKalender.add(panelUtama);
    }
 
    private void perbaruiGridKalender() {
        panelHariGrid.removeAll();
 
        String[] namaHari = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String h : namaHari) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            panelHariGrid.add(lbl);
        }
 
        SimpleDateFormat sdfHeader = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));
        lblBulanTahun.setText(sdfHeader.format(kalenderAktif.getTime()));
 
        Calendar kalTampil = (Calendar) kalenderAktif.clone();
        kalTampil.set(Calendar.DAY_OF_MONTH, 1);
        int offset = kalTampil.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Minggu
        int jumlahHari = kalTampil.getActualMaximum(Calendar.DAY_OF_MONTH);
 
        for (int i = 0; i < offset; i++) {
            panelHariGrid.add(new JLabel(""));
        }
 
        for (int tgl = 1; tgl <= jumlahHari; tgl++) {
            final int tglFinal = tgl;
            JButton btnTgl = new JButton(String.valueOf(tgl));
            btnTgl.setMargin(new Insets(2, 2, 2, 2));
            btnTgl.addActionListener(e -> {
                Calendar pilih = (Calendar) kalenderAktif.clone();
                pilih.set(Calendar.DAY_OF_MONTH, tglFinal);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtTglLahir.setText(sdf.format(pilih.getTime()));
                popupKalender.setVisible(false);
            });
            panelHariGrid.add(btnTgl);
        }
 
        popupKalender.pack();
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
        if (!validasiTglLahir()) {
            return;
        }
 
        String jkDb = cmbJenisKelamin.getSelectedItem().toString().equals("Laki-laki") ? "L" : "P";
        String tgl = txtTglLahir.getText().trim();
 
        Pasien p = new Pasien();
        p.setNamaPasien(txtNamaPasien.getText().trim());
        p.setJk(jkDb);
        p.setTglLahir((tgl.isEmpty() || tgl.equalsIgnoreCase(PLACEHOLDER_TGL)) ? null : tgl);
        p.setNoTelp(txtTelepon.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
 
        if (modeUbah) {
            // ===== MODE UPDATE data yang sudah ada =====
            p.setIdPasien(idPasienSedangDiubah);
 
            if (pasienDao.updatePasien(p)) {
                JOptionPane.showMessageDialog(this, "Data Pasien berhasil diperbarui!");
                setPasienTerpilih(p.getIdPasien(), p.getNamaPasien());
                tampilkanDataTabel();
                formBaru();
            }
        } else {
            // ===== MODE SIMPAN data baru, pakai ID yang sudah digenerate otomatis =====
            int idBaru;
            try {
                idBaru = Integer.parseInt(txtIdPasien.getText().trim());
            } catch (NumberFormatException e) {
                idBaru = pasienDao.generateIdPasien();
                txtIdPasien.setText(String.valueOf(idBaru));
            }
            if (idBaru <= 0) {
                JOptionPane.showMessageDialog(this, "Gagal membuat ID Pasien baru. Periksa koneksi database.");
                return;
            }
 
            p.setIdPasien(idBaru);
 
            if (pasienDao.tambahPasien(p)) {
                JOptionPane.showMessageDialog(this, "Data Pasien berhasil disimpan!");
                setPasienTerpilih(p.getIdPasien(), p.getNamaPasien());
                tampilkanDataTabel();
                formBaru();
            }
        }
    }//GEN-LAST:btnSimpanActionPerformed
 
    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:btnHapusActionPerformed
        if (!modeUbah || idPasienSedangDiubah <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih data pasien pada tabel terlebih dahulu!");
            return;
        }
 
        int id = idPasienSedangDiubah;
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data pasien dengan ID " + id + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (pasienDao.hapusPasien(id)) {
                JOptionPane.showMessageDialog(this, "Data Pasien berhasil dihapus! Catatan: ID " + id + " tidak akan digunakan kembali.");
                if (idPasienTerpilih == id) {
                    setPasienTerpilih(-1, "");
                }
                tampilkanDataTabel();
                formBaru();
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
 
        String[] kolom = {"ID Pasien", "Nama Pasien", "Jenis Kelamin", "Tanggal Lahir", "Alamat", "No. Telepon"};
        DefaultTableModel model = new DefaultTableModel(null, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        // Pencarian bisa berdasarkan ID Pasien ATAU Nama Pasien
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