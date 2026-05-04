package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.LoaiPhong;
import entity.Phong;
import entity.TinhTrangPhong;

public class Phong_DAO {

    private Connection con;

    public Phong_DAO() {
        con = ConnectDB.getInstance().getConnection();
    }

    private Phong parsePhong(ResultSet rs) throws SQLException {
        String ma = rs.getString("maPhong");
        String ten = rs.getString("tenPhong");
        String loaiStr = rs.getString("loaiPhong");
        int sucChua = rs.getInt("sucChua");
        String loaiGiuong = rs.getString("loaiGiuong");
        double gia = rs.getDouble("giaPhong");
        String tinhTrangStr = rs.getString("tinhTrang");

        LoaiPhong loai = LoaiPhong.fromString(loaiStr);
        TinhTrangPhong tinhTrang = TinhTrangPhong.fromString(tinhTrangStr);

        return new Phong(ma, ten, loai, sucChua, loaiGiuong, gia, tinhTrang);
    }

    public List<Phong> getDSPhong() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT maPhong, tenPhong, loaiPhong, sucChua, loaiGiuong, giaPhong, tinhTrang FROM Phong";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(parsePhong(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách phòng: " + e.getMessage());
        }
        return list;
    }

    public List<Phong> getDanhSachPhong() {
        return getDSPhong();
    }

    public boolean themPhong(Phong phong) {
        int n = 0;
        String sql = "INSERT INTO Phong (maPhong, tenPhong, loaiPhong, sucChua, loaiGiuong, giaPhong, tinhTrang) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, phong.getMaPhong());
            stmt.setString(2, phong.getTenPhong());
            stmt.setString(3, phong.getLoaiPhong().name());
            stmt.setInt(4, phong.getSucChua());
            stmt.setString(5, phong.getLoaiGiuong());
            stmt.setDouble(6, phong.getGiaPhong());
            stmt.setString(7, phong.getTinhTrang().toString()); 

            n = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thêm phòng: " + e.getMessage());
        }
        return n > 0;
    }

    public boolean capNhatPhong(Phong phong) {
        int n = 0;
        String sql = "UPDATE Phong SET tenPhong=?, loaiPhong=?, sucChua=?, loaiGiuong=?, giaPhong=?, tinhTrang=? "
                   + "WHERE maPhong=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, phong.getTenPhong());
            stmt.setString(2, phong.getLoaiPhong().name());
            stmt.setInt(3, phong.getSucChua());
            stmt.setString(4, phong.getLoaiGiuong());
            stmt.setDouble(5, phong.getGiaPhong());
            stmt.setString(6, phong.getTinhTrang().toString()); 
            stmt.setString(7, phong.getMaPhong());

            n = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật phòng: " + e.getMessage());
        }
        return n > 0;
    }

    public boolean xoaPhong(String maPhong) {
        int n = 0;
        String sql = "DELETE FROM Phong WHERE maPhong=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhong);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi xóa phòng: " + e.getMessage());
        }
        return n > 0;
    }

    public Phong timPhongTheoMa(String maPhong) {
        Phong phong = null;
        String sql = "SELECT * FROM Phong WHERE maPhong=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maPhong);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    phong = parsePhong(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm phòng: " + e.getMessage());
        }
        return phong;
    }
    
    public Phong getPhongTheoMa(String ma) {
        return timPhongTheoMa(ma);
    }

    public boolean capNhatTrangThaiPhong(String maPhong, TinhTrangPhong trangThaiMoi) {
        int n = 0;
        String sql = "UPDATE Phong SET tinhTrang = ? WHERE maPhong = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi.toString());
            stmt.setString(2, maPhong);
            n = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật trạng thái phòng: " + e.getMessage());
        }
        return n > 0;
    }

    public boolean capNhatTrangThaiPhong(String maPhong, TinhTrangPhong trangThaiMoi, Connection transCon) throws SQLException {
        int n = 0;
        String sql = "UPDATE Phong SET tinhTrang = ? WHERE maPhong = ?";
        PreparedStatement stmt = transCon.prepareStatement(sql);
        stmt.setString(1, trangThaiMoi.toString());
        stmt.setString(2, maPhong);
        n = stmt.executeUpdate();
        return n > 0;
    }
    
    // ==========================================================================
    // ========== DÀNH CHO CHONPHONG_GUI ========================================
    // ==========================================================================

    /**
     * ✅ Đã đổi tên từ getPhongTheoTieuChi -> getPhongChonPhong
     * Lọc danh sách phòng còn trống trong khoảng thời gian [checkIn, checkOut]
     * và (tùy chọn) theo khoảng giá [priceMin, priceMax].
     */
    public List<Phong> getPhongChonPhong(java.util.Date checkIn, java.util.Date checkOut, double priceMin, double priceMax) {
        List<Phong> list = new ArrayList<>();
        if (checkIn == null || checkOut == null) return list;

        java.sql.Timestamp sqlCheckIn = new java.sql.Timestamp(checkIn.getTime());
        java.sql.Timestamp sqlCheckOut = new java.sql.Timestamp(checkOut.getTime());

        String sql = "SELECT * FROM Phong P " +
                     "WHERE P.tinhTrang NOT IN (N'Đang sửa chữa', N'Ngưng hoạt động') " +
                     "AND NOT EXISTS ( " +
                     "    SELECT 1 FROM ChiTietDatPhong CT " + 
                     "    JOIN PhieuDatPhong PDP ON CT.maPhieu = PDP.maPhieu " +
                     "    WHERE CT.maPhong = P.maPhong " +
                     "    AND PDP.trangThai NOT IN (N'Đã hủy', N'Đã trả phòng') " +
                     "    AND (PDP.ngayNhan < ? AND PDP.ngayTra > ?) " +
                     ")";

        if (priceMin >= 0 && priceMax > 0 && priceMax >= priceMin) {
            sql += " AND P.giaPhong BETWEEN ? AND ?";
        }
        
        sql += " ORDER BY P.maPhong";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            int i = 1;
            stmt.setTimestamp(i++, sqlCheckOut);
            stmt.setTimestamp(i++, sqlCheckIn);

            if (priceMin >= 0 && priceMax > 0 && priceMax >= priceMin) {
                stmt.setDouble(i++, priceMin);
                stmt.setDouble(i++, priceMax);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(parsePhong(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lọc phòng (getPhongChonPhong): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    public double[] getMinMaxGiaPhong() {
        double[] minMax = {0, 10000000};
        String sql = "SELECT MIN(giaPhong), MAX(giaPhong) FROM Phong";
        try (java.sql.Statement stmt = con.createStatement(); 
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                minMax[0] = rs.getDouble(1);
                minMax[1] = rs.getDouble(2);
                if (minMax[1] <= minMax[0]) minMax[1] = minMax[0] + 100000; 
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return minMax;
    }   
    
    public List<Phong> getPhongTheoTieuChi(java.util.Date checkIn, java.util.Date checkOut, double priceMin,
            double priceMax) {
        List<Phong> list = new ArrayList<>();

        // <<< SỬA LỖI: Dùng CTE (WITH ... AS) cho SQL Server >>>
        String sql = "WITH booked_rooms AS ( " +
                     "    SELECT DISTINCT ct.maPhong " +
                     "    FROM ChiTietDatPhong ct " +
                     "    JOIN PhieuDatPhong pdp ON ct.maPhieu = pdp.maPhieu " +
                     "    WHERE pdp.trangThai NOT IN (N'Đã hủy', N'Hủy') " +
                     "      AND pdp.ngayNhan < ? " +  // (tham số 1: checkOut)
                     "      AND pdp.ngayTra > ? " +   // (tham số 2: checkIn)
                     ") " +
                     "SELECT p.* " +
                     "FROM Phong p " +
                     "LEFT JOIN booked_rooms ON p.maPhong = booked_rooms.maPhong " +
                     "WHERE " +
                     "    booked_rooms.maPhong IS NULL " + // Chỉ lấy phòng KHÔNG BỊ TRÙNG
                     "    AND p.giaPhong BETWEEN ? AND ? ";  // (tham số 3: priceMin, 4: priceMax)

        try (PreparedStatement stmt = this.con.prepareStatement(sql)) {
            
            // Thứ tự set tham số phải khớp với câu SQL
            stmt.setDate(1, new java.sql.Date(checkOut.getTime())); // pdp.ngayNhan < ?
            stmt.setDate(2, new java.sql.Date(checkIn.getTime()));  // pdp.ngayTra > ?
            stmt.setDouble(3, priceMin);                            // p.giaPhong BETWEEN ?
            stmt.setDouble(4, priceMax);                            // AND ?

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(parsePhong(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tìm phòng theo tiêu chí (đã tối ưu CTE): " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
             System.err.println("❌ Lỗi parse Enum (kiểm tra CSDL): " + e.getMessage());
        }
        return list;
    }
    
    /**
     * ĐÃ SỬA LỖI:
     * 1. Dùng this.con (biến instance)
     * 2. Dùng try-with-resources
     */
    public boolean isPhongAvailable(String maPhong, java.util.Date selIn, java.util.Date selOut) {
        
        String sql = 
            "SELECT COUNT(*) FROM PhieuDatPhong pd " +
            "JOIN ChiTietDatPhong ct ON pd.maPhieu = ct.maPhieu " +
            "WHERE ct.maPhong = ? " +
            "  AND pd.trangThai NOT IN (N'Hủy', N'Đã hủy') " + 
            "  AND NOT (pd.ngayTra <= ? OR pd.ngayNhan >= ?)"; 

        try (PreparedStatement stmt = this.con.prepareStatement(sql)) { 
            
            stmt.setString(1, maPhong);
            stmt.setDate(2, new java.sql.Date(selIn.getTime()));   // pd.ngayTra <= checkIn
            stmt.setDate(3, new java.sql.Date(selOut.getTime()));  // pd.ngayNhan >= checkOut

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    System.out.println("[isPhongAvailable] " + maPhong + " from " + selIn + " to " + selOut + " -> overlapCount=" + cnt);
                    return cnt == 0; 
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi kiểm tra isPhongAvailable: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false; 
    }
    public List<Phong> getDSPhongTaiThoiDiem(java.util.Date tuNgay, java.util.Date denNgay) {
        List<Phong> list = new ArrayList<>();
        // Nếu không có ngày bắt đầu, trả về trạng thái hiện tại
        if (tuNgay == null) return getDSPhong();
        
        // Nếu chỉ có ngày bắt đầu mà chưa có ngày kết thúc, mặc định xem trong 1 ngày
        if (denNgay == null) {
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.setTime(tuNgay);
            c.add(java.util.Calendar.DAY_OF_MONTH, 1);
            denNgay = c.getTime();
        }

        // Chuyển sang Timestamp để so sánh chính xác cả giờ phút (nếu cần)
        java.sql.Timestamp sqlTuNgay = new java.sql.Timestamp(tuNgay.getTime());
        java.sql.Timestamp sqlDenNgay = new java.sql.Timestamp(denNgay.getTime());

        String sql = "SELECT P.maPhong, P.tenPhong, P.loaiPhong, P.sucChua, P.loaiGiuong, P.giaPhong, " +
                     "CASE " +
                     // Trường hợp 1: Phòng đang có khách ở (Phiếu 'Đã nhận') bị trùng lịch
                     "  WHEN EXISTS (" +
                     "    SELECT 1 FROM ChiTietDatPhong CT " +
                     "    JOIN PhieuDatPhong PDP ON CT.maPhieu = PDP.maPhieu " +
                     "    WHERE CT.maPhong = P.maPhong " +
                     "    AND PDP.trangThai = N'Đã nhận' " +
                     "    AND (PDP.ngayNhan < ? AND PDP.ngayTra > ?) " + // Logic trùng lịch: (StartA < EndB) AND (EndA > StartB)
                     "  ) THEN N'Đang sử dụng' " +
                     // Trường hợp 2: Phòng đã được đặt trước (Phiếu 'Chờ nhận phòng') bị trùng lịch
                     "  WHEN EXISTS (" +
                     "    SELECT 1 FROM ChiTietDatPhong CT " +
                     "    JOIN PhieuDatPhong PDP ON CT.maPhieu = PDP.maPhieu " +
                     "    WHERE CT.maPhong = P.maPhong " +
                     "    AND PDP.trangThai IN (N'Chờ nhận phòng', N'Đã xác nhận') " +
                     "    AND (PDP.ngayNhan < ? AND PDP.ngayTra > ?) " +
                     "  ) THEN N'Đã đặt' " +
                     // Trường hợp 3: Các trạng thái vật lý đặc biệt (giữ nguyên từ DB)
                     "  WHEN P.tinhTrang IN (N'Đang sửa chữa', N'Ngưng hoạt động', N'Đang dọn dẹp') THEN P.tinhTrang " +
                     // Trường hợp còn lại: Không trùng lịch nào -> Trống
                     "  ELSE N'Trống' " +
                     "END AS tinhTrang " +
                     "FROM Phong P";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            // Set tham số cho 2 dấu hỏi đầu tiên (Trường hợp 1: Đang sử dụng)
            stmt.setTimestamp(1, sqlDenNgay); // PDP.ngayNhan < denNgay
            stmt.setTimestamp(2, sqlTuNgay);  // PDP.ngayTra > tuNgay
            
            // Set tham số cho 2 dấu hỏi tiếp theo (Trường hợp 2: Đã đặt)
            stmt.setTimestamp(3, sqlDenNgay);
            stmt.setTimestamp(4, sqlTuNgay);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(parsePhong(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy DS phòng theo khoảng thời gian: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}