/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import koneksi.Koneksi;
import model.Resep;
import java.sql.ResultSet;
import java.util.ArrayList;
/**
 *
 * @author VanZ
 */
public class ResepDao {
      public int insert(Resep r) {
        String sql = "INSERT INTO resep (id_pemeriksaan, tanggal_resep) VALUES (" 
                   + r.getIdPemeriksaan() + ", '" + r.getTanggalResep() + "')";
        return Koneksi.insertQueryGetId(sql);
    }

    public boolean update(Resep r) {
        String sql = "UPDATE resep SET id_pemeriksaan=" + r.getIdPemeriksaan() 
                   + ", tanggal_resep='" + r.getTanggalResep() + "' WHERE id_resep=" + r.getIdResep();
        return Koneksi.executeQuery(sql);
    }

    public boolean delete(int idResep) {
        new ResepDetailDao().deleteByResepId(idResep);
        String sql = "DELETE FROM resep WHERE id_resep=" + idResep;
        return Koneksi.executeQuery(sql);
    }

    public ArrayList<Resep> getAll() {
        ArrayList<Resep> list = new ArrayList<>();
        ResultSet rs = Koneksi.selectQuery("SELECT * FROM resep");
        try {
            while (rs.next()) {
                Resep r = new Resep();
                r.setIdResep(rs.getInt("id_resep"));
                r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
                r.setTanggalResep(rs.getDate("tanggal_resep"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Resep getById(int idResep) {
        Resep r = new Resep();
        ResultSet rs = Koneksi.selectQuery("SELECT * FROM resep WHERE id_resep=" + idResep);
        try {
            if (rs.next()) {
                r.setIdResep(rs.getInt("id_resep"));
                r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
                r.setTanggalResep(rs.getDate("tanggal_resep"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public ArrayList<Resep> getByPemeriksaanId(int idPemeriksaan) {
        ArrayList<Resep> list = new ArrayList<>();
        ResultSet rs = Koneksi.selectQuery("SELECT * FROM resep WHERE id_pemeriksaan=" + idPemeriksaan);
        try {
            while (rs.next()) {
                Resep r = new Resep();
                r.setIdResep(rs.getInt("id_resep"));
                r.setIdPemeriksaan(rs.getInt("id_pemeriksaan"));
                r.setTanggalResep(rs.getDate("tanggal_resep"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
