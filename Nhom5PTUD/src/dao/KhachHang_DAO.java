package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.LoaiKhachHang;

public class KhachHang_DAO {

    public KhachHang_DAO() {
        // constructor
    }

    /**
     * Lấy danh sách tất cả Khách hàng từ Database
     */
    public List<KhachHang> getAllKhachHang() {
        ArrayList<KhachHang> dsKhachHang = new ArrayList<>();
        try {
            ConnectDB.getInstance().connect();
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM KhachHang";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String maKH = rs.getString("maKhachHang");
                String hoTen = rs.getString("hoTen");
                String soDT = rs.getString("soDienThoai");
                LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                LoaiKhachHang loaiKH = LoaiKhachHang.valueOf(rs.getString("loaiKhachHang").toUpperCase());

                KhachHang kh = new KhachHang(maKH, hoTen, soDT, ngaySinh, loaiKH);
                dsKhachHang.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKhachHang;
    }

    /**
     * Thêm một Khách Hàng mới vào Database
     * Tự động tạo mã KH mới (ví dụ: KH011)
     */
    public boolean create(KhachHang kh) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        int n = 0;

        try {
            // 1. Kiểm tra SĐT trùng
            try (PreparedStatement stmtCheck = con.prepareStatement("SELECT COUNT(*) FROM KhachHang WHERE soDienThoai = ?")) {
                stmtCheck.setString(1, kh.getSoDienThoai());
                ResultSet rsCheck = stmtCheck.executeQuery();
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    return false; // Đã tồn tại SĐT
                }
            }

            // 2. Tạo mã KH mới - FIXED: Use parameterless version
            String newMaKH = generateMaKH();
            kh.setMaKhachHang(newMaKH);

            // 3. Thêm vào CSDL
            String sql = "INSERT INTO KhachHang (maKhachHang, hoTen, soDienThoai, ngaySinh, loaiKhachHang) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmtInsert = con.prepareStatement(sql)) {
                stmtInsert.setString(1, newMaKH);
                stmtInsert.setString(2, kh.getHoTen());
                stmtInsert.setString(3, kh.getSoDienThoai());
                stmtInsert.setDate(4, Date.valueOf(kh.getNgaySinh()));
                stmtInsert.setString(5, kh.getLoaiKhachHang().toString());
                n = stmtInsert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    public String generateMaKH() {
        String maKH = "KH001";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT MAX(maKhachHang) FROM KhachHang";
            
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    String maxMaKH = rs.getString(1);
                    if (maxMaKH != null && !maxMaKH.isEmpty()) {
                        int num = Integer.parseInt(maxMaKH.substring(2));
                        num++;
                        maKH = String.format("KH%03d", num);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maKH;
    }

    /**
     * Cập nhật thông tin Khách Hàng
     */
    public boolean update(KhachHang kh) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        int n = 0;

        try (PreparedStatement stmt = con.prepareStatement(
                "UPDATE KhachHang SET hoTen = ?, soDienThoai = ?, ngaySinh = ?, loaiKhachHang = ? WHERE maKhachHang = ?")) {
            stmt.setString(1, kh.getHoTen());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setDate(3, Date.valueOf(kh.getNgaySinh()));
            stmt.setString(4, kh.getLoaiKhachHang().toString());
            stmt.setString(5, kh.getMaKhachHang());
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    /**
     * Xóa Khách Hàng theo mã
     */
    public boolean delete(String maKH) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        int n = 0;

        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM KhachHang WHERE maKhachHang = ?")) {
            stmt.setString(1, maKH);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return n > 0;
    }

    /**
     * Tìm Khách Hàng theo mã
     */
    public KhachHang search(String maKH) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        KhachHang kh = null;

        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM KhachHang WHERE maKhachHang = ?")) {
            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hoTen = rs.getString("hoTen");
                String soDT = rs.getString("soDienThoai");
                LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                LoaiKhachHang loaiKH = LoaiKhachHang.valueOf(rs.getString("loaiKhachHang").toUpperCase());

                kh = new KhachHang(maKH, hoTen, soDT, ngaySinh, loaiKH);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kh;
    }
    
    public KhachHang timKhachHangTheoSDT(String soDienThoai) {
        ConnectDB.getInstance().connect();
        Connection con = ConnectDB.getConnection();
        KhachHang kh = null;

        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM KhachHang WHERE soDienThoai = ?")) {
            stmt.setString(1, soDienThoai);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String maKH = rs.getString("maKhachHang");
                String hoTen = rs.getString("hoTen");
                LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                LoaiKhachHang loaiKH = LoaiKhachHang.valueOf(rs.getString("loaiKhachHang").toUpperCase());

                kh = new KhachHang(maKH, hoTen, soDienThoai, ngaySinh, loaiKH);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kh;
    }
}