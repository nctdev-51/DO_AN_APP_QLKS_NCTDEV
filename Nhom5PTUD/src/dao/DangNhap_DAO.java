package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.LoaiNhanVien;

public class DangNhap_DAO {

    ArrayList<TaiKhoan> dstk;

    public DangNhap_DAO() {
        dstk = new ArrayList<TaiKhoan>();
    }

    public TaiKhoan ktDangNhap(String taiKhoan, String matKhau) {
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TaiKhoan tkhoan = null;
        
        try {
            String sql = "SELECT K.maNhanVien, K.taiKhoan, K.matKhau, " +
                        "V.maNhanVien, V.hoTen, V.ngaySinh, V.gioiTinh, V.CCCD, " +
                        "V.soDienThoai, V.trangThai, V.loaiNhanVien, V.ngayVaoLam, V.queQuan " +
                        "FROM TaiKhoan K JOIN NhanVien V ON K.maNhanVien = V.maNhanVien " +
                        "WHERE K.taiKhoan = ? AND K.matKhau = ? AND V.trangThai = 1";
            
            stmt = con.prepareStatement(sql);
            stmt.setString(1, taiKhoan);
            stmt.setString(2, matKhau);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Dữ liệu tài khoản
                String maNV = rs.getString("maNhanVien");
                String tenDN = rs.getString("taiKhoan");
                String mk = rs.getString("matKhau");
                
                // Dữ liệu nhân viên
                String hoTen = rs.getString("hoTen");
                LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                boolean gioiTinh = rs.getBoolean("gioiTinh");
                String CCCD = rs.getString("CCCD");
                String soDienThoai = rs.getString("soDienThoai");
                boolean trangThai = rs.getBoolean("trangThai");
                
                String loaiNhanVienStr = rs.getString("loaiNhanVien");
                LoaiNhanVien loaiNhanVien;
                try {
                    loaiNhanVien = LoaiNhanVien.valueOf(loaiNhanVienStr);
                } catch (IllegalArgumentException e) {
                    loaiNhanVien = LoaiNhanVien.NHAN_VIEN_LE_TAN;
                }
                
                LocalDate ngayVaoLam = rs.getDate("ngayVaoLam").toLocalDate();
                String queQuan = rs.getString("queQuan");

                NhanVien nvien = new NhanVien(maNV, hoTen, ngaySinh, gioiTinh, CCCD, 
                                             soDienThoai, trangThai, loaiNhanVien, ngayVaoLam, queQuan);
                tkhoan = new TaiKhoan(nvien, tenDN, mk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL trong đăng nhập: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tkhoan;
    }
}
