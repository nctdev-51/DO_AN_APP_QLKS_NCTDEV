package dao;

import java.sql.*;
import java.time.LocalDate;
import connectDB.ConnectDB;
import entity.PhieuDatPhong;
import entity.NhanVien;

public class HoaDon_DAO {/*

    public boolean taoHoaDonTuPhieuDat(PhieuDatPhong pdp, NhanVien nv) {
        Connection con = null;
        PreparedStatement psHD = null;
        PreparedStatement psPhong = null;
        PreparedStatement psPhieu = null;

        try {
            con = ConnectDB.getInstance().getConnection();
            con.setAutoCommit(false);

            // ✅ Sinh mã Hóa Đơn
            String maHD = "HD" + System.currentTimeMillis();

            // ✅ Insert bảng HoaDon
            String sql = "INSERT INTO HoaDon "
                       + "(maHoaDon, maNhanVien, maKhachHang, ngayLap, maPhongDat, thueVAT, tongTien) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            psHD = con.prepareStatement(sql);
            psHD.setString(1, maHD);
            psHD.setString(2, nv.getMaNhanVien()); // ✅ Đúng nhân viên
            psHD.setString(3, pdp.getKhachHang().getMaKhachHang());
            psHD.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            psHD.setString(5, pdp.getPhong().getMaPhong());
            psHD.setDouble(6, 10); 
            psHD.setDouble(7, pdp.getTongTien());
            psHD.executeUpdate();

            // ✅ Update Phòng
            psPhong = con.prepareStatement(
                "UPDATE Phong SET tinhTrang = N'Đang Sử Dụng' WHERE maPhong = ?"
            );
            psPhong.setString(1, pdp.getPhong().getMaPhong());
            psPhong.executeUpdate();

            // ✅ Update PhieuDatPhong
            psPhieu = con.prepareStatement(
                "UPDATE PhieuDatPhong SET trangThai = N'Đã Check-in' WHERE maPhieu = ?"
            );
            psPhieu.setString(1, pdp.getMaPhieu());
            psPhieu.executeUpdate();

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        }
        return false;
    }*/
}
