package dao;

import entity.DichVu;
import entity.LoaiDichVu; // <<< THÊM IMPORT
import entity.DonViTinh; // <<< THÊM IMPORT
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;

public class DichVu_DAO {
    private Connection connection;

    public DichVu_DAO() {
        this.connection = ConnectDB.getInstance().getConnection();
    }

    // --- NÂNG CẤP: Hàm parse dùng Enum ---
    private DichVu parseDichVu(ResultSet rs) throws SQLException {
        String maDichVu = rs.getString("maDichVu");
        String tenDichVu = rs.getString("tenDichVu");
        double giaTien = rs.getDouble("giaTien");
        String moTa = rs.getString("moTa");
        
        // Lấy chuỗi từ CSDL
        String loaiDichVuStr = rs.getString("loaiDichVu"); 
        String donViTinhStr = rs.getString("donViTinh");
        String hinhAnh = rs.getString("hinhAnh");

        // Chuyển đổi chuỗi thành Enum
        LoaiDichVu loai = LoaiDichVu.fromString(loaiDichVuStr);
        DonViTinh dvt = DonViTinh.fromString(donViTinhStr);

        if (hinhAnh == null || hinhAnh.isEmpty()) hinhAnh = "data/services/default.png";

        // Dùng constructor mới
        return new DichVu(maDichVu, tenDichVu, giaTien, moTa, loai, dvt, hinhAnh);
    }

    // Lấy tất cả dịch vụ (Không cần sửa, vì nó gọi parseDichVu)
    public List<DichVu> getAllDichVu() {
        List<DichVu> dsDichVu = new ArrayList<>();
        String sql = "SELECT maDichVu, tenDichVu, giaTien, moTa, loaiDichVu, donViTinh, hinhAnh FROM DichVu";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                dsDichVu.add(parseDichVu(rs)); // Dùng hàm parse đã sửa
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách dịch vụ từ database", e);
        }
        return dsDichVu;
    }

    // Tìm dịch vụ theo mã (Không cần sửa, vì nó gọi parseDichVu)
    public DichVu getDichVuByMa(String maDichVu) {
        String sql = "SELECT maDichVu, tenDichVu, giaTien, moTa, loaiDichVu, donViTinh, hinhAnh FROM DichVu WHERE maDichVu = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maDichVu);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return parseDichVu(rs); // Dùng hàm parse đã sửa
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm dịch vụ mới
    public boolean addDichVu(DichVu dichVu) {
        String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, giaTien, moTa, loaiDichVu, donViTinh, hinhAnh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dichVu.getMaDichVu());
            stmt.setString(2, dichVu.getTenDichVu());
            stmt.setDouble(3, dichVu.getGiaTien());
            stmt.setString(4, dichVu.getMoTa());
            
            // --- THAY ĐỔI: Lưu Enum.toString() (ví dụ: "Thức ăn") ---
            stmt.setString(5, dichVu.getLoaiDichVu().toString());
            stmt.setString(6, dichVu.getDonViTinh().toString());
            stmt.setString(7, dichVu.getHinhAnh());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật dịch vụ
    public boolean updateDichVu(DichVu dichVu) {
        String sql = "UPDATE DichVu SET tenDichVu = ?, giaTien = ?, moTa = ?, loaiDichVu = ?, donViTinh = ?, hinhAnh = ? WHERE maDichVu = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dichVu.getTenDichVu());
            stmt.setDouble(2, dichVu.getGiaTien());
            stmt.setString(3, dichVu.getMoTa());
            
            // --- THAY ĐỔI: Lưu Enum.toString() (ví dụ: "Thức ăn") ---
            stmt.setString(4, dichVu.getLoaiDichVu().toString());
            stmt.setString(5, dichVu.getDonViTinh().toString());
            stmt.setString(6, dichVu.getHinhAnh());
            stmt.setString(7, dichVu.getMaDichVu());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // (Các hàm còn lại: deleteDichVu, searchDichVu, isMaDichVuExists giữ nguyên)
    
    // Xóa dịch vụ
    public boolean deleteDichVu(String maDichVu) {
        String sql = "DELETE FROM DichVu WHERE maDichVu = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maDichVu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm dịch vụ theo mã (dùng cho tìm kiếm)
    public DichVu searchDichVu(String maDichVu) {
        return getDichVuByMa(maDichVu);
    }

    // Kiểm tra mã dịch vụ đã tồn tại chưa
    public boolean isMaDichVuExists(String maDichVu) {
        String sql = "SELECT 1 FROM DichVu WHERE maDichVu = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maDichVu);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}