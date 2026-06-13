/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {

    private static final String URL = "jdbc:mysql://localhost:3306/dbklinik";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getKoneksi() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi database gagal: " + e.getMessage());
        }
        return conn;
    }
}