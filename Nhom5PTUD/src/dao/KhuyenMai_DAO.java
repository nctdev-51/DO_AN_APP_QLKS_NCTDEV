package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.KhuyenMai;
import entity.LoaiKhuyenMai;

public class KhuyenMai_DAO {

    private String generateNewID() throws SQLException {
        Connection con = ConnectDB.getConnection();
        String sql = "SELECT MAX(maKhuyenMai) AS maxID FROM KhuyenMai";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        String newID = "KM001";
        if (rs.next() && rs.getString("maxID") != null) {
            String maxID = rs.getString("maxID");
            int num = Integer.parseInt(maxID.substring(2)) + 1;
            newID = String.format("KM%03d", num);
        }
        rs.close();
        stmt.close();
        return newID;
    }

    public boolean insertKhuyenMai(KhuyenMai km) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().trim().isEmpty()) {
                km.setMaKhuyenMai(generateNewID());
            }

            String sql = "INSERT INTO KhuyenMai VALUES (?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getMaKhuyenMai());
            stmt.setString(2, km.getTenKhuyenMai());
            stmt.setDate(3, Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            stmt.setString(5, km.getLoaiKhuyenMai().name());
            stmt.setDouble(6, km.getChietKhau());
            
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
        return n > 0;
    }

    public boolean updateKhuyenMai(KhuyenMai km) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "UPDATE KhuyenMai SET tenKhuyenMai=?, ngayBatDau=?, ngayKetThuc=?, loaiKhuyenMai=?, chietKhau=? WHERE maKhuyenMai=?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getTenKhuyenMai());
            stmt.setDate(2, Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(3, Date.valueOf(km.getNgayKetThuc()));
            stmt.setString(4, km.getLoaiKhuyenMai().name());
            stmt.setDouble(5, km.getChietKhau());
            stmt.setString(6, km.getMaKhuyenMai());

            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
        return n > 0;
    }

    public boolean deleteKhuyenMai(String maKM) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Không thể xóa khuyến mãi do đang được sử dụng ở bảng khác!");
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
        return n > 0;
    }

    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> dsKM = new ArrayList<>();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM KhuyenMai";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                dsKM.add(new KhuyenMai(
                        rs.getString("maKhuyenMai"),
                        rs.getString("tenKhuyenMai"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        LoaiKhuyenMai.valueOf(rs.getString("loaiKhuyenMai")),
                        rs.getDouble("chietKhau")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {}
        }
        return dsKM;
    }

    public KhuyenMai getKhuyenMaiTheoMa(String maKM) {
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        KhuyenMai km = null;
        try {
            String sql = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            rs = stmt.executeQuery();
            if (rs.next()) {
                km = new KhuyenMai(
                        maKM,
                        rs.getString("tenKhuyenMai"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        LoaiKhuyenMai.valueOf(rs.getString("loaiKhuyenMai")),
                        rs.getDouble("chietKhau"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
        return km;
    }

    // Bridge method tương thích GUI
    public boolean create(KhuyenMai km) { return insertKhuyenMai(km); }
    public boolean update(KhuyenMai km) { return updateKhuyenMai(km); }
    public boolean delete(String ma) { return deleteKhuyenMai(ma); }
    public KhuyenMai search(String ma) { return getKhuyenMaiTheoMa(ma); }
}
