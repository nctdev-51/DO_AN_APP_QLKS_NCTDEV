package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import connectDB.ConnectDB;
import entity.ChiTietDichVu;

public class ChiTietDichVu_DAO {
    private Connection con;

    public ChiTietDichVu_DAO() {
        con = ConnectDB.getInstance().getConnection();
    }

    /**
     * Thêm một dịch vụ được gọi vào CSDL
     * @param ctdv Đối tượng ChiTietDichVu (chứa maPhieu, maPhong, maDichVu,...)
     * @return true nếu thêm thành công
     */
    public boolean addChiTietDichVu(ChiTietDichVu ctdv) {
        // CSDL mới yêu cầu 6 cột
        String sql = "INSERT INTO ChiTietDichVu (maPhieu, maPhong, maDichVu, thoiGianGoi, soLuong, ghiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ctdv.getPhieuDatPhong().getMaPhieu());
            stmt.setString(2, ctdv.getPhong().getMaPhong());
            stmt.setString(3, ctdv.getDichVu().getMaDichVu());
            stmt.setTimestamp(4, Timestamp.valueOf(ctdv.getThoiGianGoi()));
            stmt.setInt(5, ctdv.getSoLuong());
            stmt.setString(6, ctdv.getGhiChu());
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}