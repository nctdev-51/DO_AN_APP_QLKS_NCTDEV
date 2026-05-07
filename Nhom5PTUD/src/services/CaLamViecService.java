package services;

import java.sql.*;
import connectDB.ConnectDB;

public class CaLamViecService {

    private Connection getConnection() throws SQLException {
        return ConnectDB.getInstance().getConnection();
    }

    /**
     * (Quản lý) Gọi sp_PhanCongCa
     */
    public void phanCongCa(String maNhanVien, String maCa, java.sql.Date ngayLamViec) throws SQLException {
        String sql = "{CALL sp_PhanCongCa(?, ?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, maNhanVien);
            cstmt.setString(2, maCa);
            cstmt.setDate(3, ngayLamViec);
            cstmt.execute();
        }
    }

    /**
     * (Nhân viên) Gọi sp_NhanCa
     */
    public void nhanCa(String maLichSu, double tienMatDauCa) throws SQLException {
        String sql = "{CALL sp_NhanCa(?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, maLichSu);
            cstmt.setDouble(2, tienMatDauCa);
            cstmt.execute();
        }
    }

    /**
     * (Nhân viên) Gọi sp_BanGiaoCa
     */
    public void banGiaoCa(String maLichSu, double tienCuoi, double thuDV, double thuPhong, double tongChi) throws SQLException {
        String sql = "{CALL sp_BanGiaoCa(?, ?, ?, ?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, maLichSu);
            cstmt.setDouble(2, tienCuoi);
            cstmt.setDouble(3, thuDV);
            cstmt.setDouble(4, thuPhong);
            cstmt.setDouble(5, tongChi);
            cstmt.execute();
        }
    }
}