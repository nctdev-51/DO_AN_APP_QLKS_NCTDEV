package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import connectDB.ConnectDB;
import entity.CaLamViec;
import entity.LichSuCaLamViec;
import entity.NhanVien;

public class LichSuCaLamViec_DAO {
    private Connection con;
    private NhanVien_DAO nv_dao;
    private CaLamViec_DAO ca_dao;

    public LichSuCaLamViec_DAO() {
        con = ConnectDB.getInstance().getConnection();
        nv_dao = new NhanVien_DAO();
        ca_dao = new CaLamViec_DAO();
    }
    
    // Hàm helper để parse ResultSet (Giữ nguyên)
    private LichSuCaLamViec parseResultSet(ResultSet rs) throws SQLException {
        String maLS = rs.getString("maLichSu");
        String maNV = rs.getString("maNhanVien");
        String maCa = rs.getString("maCa");
        LocalDate ngayLamViec = rs.getDate("ngayLamViec") != null ? rs.getDate("ngayLamViec").toLocalDate() : null;
        LocalDateTime thoiGianNhanCa = rs.getTimestamp("thoiGianNhanCa") != null ? rs.getTimestamp("thoiGianNhanCa").toLocalDateTime() : null;
        LocalDateTime thoiGianBanGiao = rs.getTimestamp("thoiGianBanGiao") != null ? rs.getTimestamp("thoiGianBanGiao").toLocalDateTime() : null;
        double tienMatDauCa = rs.getDouble("tienMatDauCa");
        double tienMatCuoiCa = rs.getDouble("tienMatCuoiCa");
        double tongThuDV = rs.getDouble("tongThuDichVu");
        double tongThuPhong = rs.getDouble("tongThuPhong");
        double tongChi = rs.getDouble("tongChi");

        NhanVien nv = nv_dao.getNhanVienByMa(maNV);
        CaLamViec ca = ca_dao.getCaLamViecByMa(maCa);

        LichSuCaLamViec ls = new LichSuCaLamViec(maLS, nv, ca, ngayLamViec, thoiGianNhanCa, thoiGianBanGiao, tienMatDauCa, tienMatCuoiCa, tongThuDV, tongThuPhong, tongChi);
        
        if (ca == null && maCa != null) {
            ls.setCaLamViec(new CaLamViec(maCa, "CA BỊ XÓA", null, null, "Ca đã bị xóa khỏi hệ thống", false));
        }
        
        return ls;
    }

    // Lấy tất cả lịch sử (để hiển thị) (Giữ nguyên)
    public List<LichSuCaLamViec> getAllLichSu() {
        List<LichSuCaLamViec> dsLS = new ArrayList<>();
        String sql = "SELECT * FROM LichSuCaLamViec ORDER BY ngayLamViec DESC, thoiGianNhanCa DESC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsLS.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsLS;
    }
    
    // Lấy các ca CHƯA diễn ra (để quản lý phân công) (Giữ nguyên)
    public List<LichSuCaLamViec> getLichPhanCongTuHomNay() {
        List<LichSuCaLamViec> dsLS = new ArrayList<>();
        String sql = "SELECT * FROM LichSuCaLamViec "
                   + "WHERE ngayLamViec >= CAST(GETDATE() AS DATE) "
                   + "ORDER BY ngayLamViec ASC, thoiGianNhanCa ASC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsLS.add(parseResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsLS;
    }

    // Thêm một phân công mới (Giữ nguyên)
    public boolean addPhanCong(LichSuCaLamViec ls) {
        String sql = "INSERT INTO LichSuCaLamViec (maLichSu, maNhanVien, maCa, ngayLamViec) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ls.getMaLichSu());
            stmt.setString(2, ls.getNhanVien().getMaNhanVien());
            stmt.setString(3, ls.getCaLamViec().getMaCa());
            stmt.setDate(4, Date.valueOf(ls.getNgayLamViec()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Xóa một phân công (chỉ xóa khi chưa nhận ca) (Giữ nguyên)
    public boolean deletePhanCong(String maLichSu) {
        String sql = "DELETE FROM LichSuCaLamViec WHERE maLichSu = ? AND thoiGianNhanCa IS NULL";
         try (PreparedStatement stmt = con.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // <<< SỬA LỖI CÚ PHÁP: THAY THẾ TOÀN BỘ HÀM NÀY >>>
    /**
     * Tìm ca làm việc HIỆN TẠI của nhân viên (để Nhận/Bàn Giao).
     * SỬA LỖI: Đã thay thế cú pháp DATEADD lồng CASE bằng CASE bên ngoài
     * để tương thích với SQL Server 2008.
     */
    public LichSuCaLamViec getCaLamViecHienTai(String maNV) {
        
        String sql = "WITH RankedCa AS (" +
            "    SELECT " +
            "        ls.*, " +
            "        ca.thoiGianBatDau, " +
            "        ca.thoiGianKetThuc," +
            
            // 1. Tính toán thời gian bắt đầu thực tế (vẫn giữ nguyên)
            "        CAST(ls.ngayLamViec AS DATETIME) + CAST(ISNULL(ca.thoiGianBatDau, '00:00:00') AS DATETIME) AS TrueStartTime," +
            
            // 2. <<< ĐÂY LÀ DÒNG ĐÃ SỬA LỖI >>>
            // Sử dụng CASE bên ngoài DATEADD để tương thích SQL Server 2008
            "        CASE " +
            "            WHEN ca.thoiGianBatDau > ca.thoiGianKetThuc THEN " + // Ca đêm
            "                DATEADD(day, 1, CAST(ls.ngayLamViec AS DATETIME) + CAST(ISNULL(ca.thoiGianKetThuc, '00:00:00') AS DATETIME)) " +
            "            ELSE " + // Ca ngày
            "                CAST(ls.ngayLamViec AS DATETIME) + CAST(ISNULL(ca.thoiGianKetThuc, '00:00:00') AS DATETIME) " +
            "        END AS TrueEndTime " +
            
            "    FROM LichSuCaLamViec ls " +
            "    LEFT JOIN CaLamViec ca ON ls.maCa = ca.maCa " +
            "    WHERE " +
            "        ls.maNhanVien = ? AND " +
            "        (" +
            "            (ls.thoiGianNhanCa IS NOT NULL AND ls.thoiGianBanGiao IS NULL) " +
            "            OR " +
            "            (" +
            "                ls.thoiGianNhanCa IS NULL AND " +
            "                ls.ngayLamViec >= CAST(DATEADD(day, -1, GETDATE()) AS DATE) AND " +
            "                ls.ngayLamViec <= CAST(GETDATE() AS DATE)" +
            "            )" +
            "        )" +
            "), " +
            "PrioritizedCa AS (" +
            "    SELECT " +
            "        *," +
            "        CASE" +
            "            WHEN (thoiGianNhanCa IS NOT NULL AND thoiGianBanGiao IS NULL) THEN 1" +
            "            WHEN (thoiGianNhanCa IS NULL AND maCa IS NOT NULL AND " + 
            "                  GETDATE() >= DATEADD(minute, -30, TrueStartTime) AND " + 
            "                  GETDATE() < TrueEndTime) THEN 2" + 
            "            ELSE 3 " +
            "        END AS UuTien " +
            "    FROM RankedCa" +
            ")" +
            "SELECT TOP 1 * " +
            "FROM PrioritizedCa " +
            "WHERE UuTien IN (1, 2) " +
            "ORDER BY " +
            "    UuTien ASC, " +
            "    TrueStartTime DESC";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            // Đây là dòng 170
            ResultSet rs = stmt.executeQuery(); 
            if (rs.next()) {
                return parseResultSet(rs);
            }
        } catch (SQLException e) {
            // In ra lỗi chi tiết để gỡ rối
            System.err.println("SQL Lỗi tại getCaLamViecHienTai: " + e.getMessage());
            e.printStackTrace(); 
        }
        return null; // Không có ca nào
    }
    // <<< KẾT THÚC THAY THẾ >>>
    
    // Cập nhật khi NHẬN CA (Giữ nguyên)
    public boolean nhanCa(String maLichSu, double tienMatDauCa) {
        String sql = "UPDATE LichSuCaLamViec SET thoiGianNhanCa = ?, tienMatDauCa = ? WHERE maLichSu = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(2, tienMatDauCa);
            stmt.setString(3, maLichSu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật khi BÀN GIAO CA (Giữ nguyên)
    public boolean banGiaoCa(LichSuCaLamViec ls) {
        String sql = "UPDATE LichSuCaLamViec SET thoiGianBanGiao = ?, tienMatCuoiCa = ?, tongThuDichVu = ?, tongThuPhong = ?, tongChi = ? "
                   + "WHERE maLichSu = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); 
            stmt.setDouble(2, ls.getTienMatCuoiCa());
            stmt.setDouble(3, ls.getTongThuDichVu());
            stmt.setDouble(4, ls.getTongThuPhong());
            stmt.setDouble(5, ls.getTongChi());
            stmt.setString(6, ls.getMaLichSu());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Hàm hoán đổi (Giữ nguyên)
    public boolean hoanDoiPhanCong(LichSuCaLamViec phanCongCu, NhanVien nvMoi) {
        CaLamViec ca = phanCongCu.getCaLamViec();
        LocalDate ngayLam = phanCongCu.getNgayLamViec();
        
        if (ca == null || ca.getMaCa() == null) {
            System.err.println("Không thể hoán đổi ca mồ côi (CaLamViec đã bị xóa).");
            return false;
        }
        
        String maLSMoi = "LS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql_Insert = "INSERT INTO LichSuCaLamViec (maLichSu, maNhanVien, maCa, ngayLamViec) VALUES (?, ?, ?, ?)";
        String sql_Delete = "DELETE FROM LichSuCaLamViec WHERE maLichSu = ? AND thoiGianNhanCa IS NULL";

        try {
            con.setAutoCommit(false); 

            try (PreparedStatement stmtInsert = con.prepareStatement(sql_Insert)) {
                stmtInsert.setString(1, maLSMoi);
                stmtInsert.setString(2, nvMoi.getMaNhanVien());
                stmtInsert.setString(3, ca.getMaCa());
                stmtInsert.setDate(4, Date.valueOf(ngayLam));
                stmtInsert.executeUpdate();
            }

            try (PreparedStatement stmtDelete = con.prepareStatement(sql_Delete)) {
                stmtDelete.setString(1, phanCongCu.getMaLichSu());
                int rowsDeleted = stmtDelete.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new SQLException("Không thể hoán đổi ca đã được nhận.");
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                con.rollback(); 
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
 
    // Hàm tính tổng thu (Giữ nguyên)
    public double getTongThuPhongTrongCa(String maNhanVien, LocalDateTime thoiGianNhanCa) {
        double tongThu = 0;
        String sql = "SELECT SUM(ISNULL(hd.tongTien, 0) - ISNULL(dv.TongTienDV, 0)) " +
                     "FROM HoaDon hd " +
                     "LEFT JOIN ( " +
                     "    SELECT maHoaDon, SUM(ct.soLuong * d.giaTien) AS TongTienDV " +
                     "    FROM ChiTietHoaDon ct " +
                     "    JOIN DichVu d ON ct.maDichVu = d.maDichVu " +
                     "    GROUP BY maHoaDon " +
                     ") dv ON hd.maHoaDon = dv.maHoaDon " +
                     "WHERE hd.maNhanVien = ? AND hd.ngayLap >= ? AND hd.ngayLap < ?"; 

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setTimestamp(2, Timestamp.valueOf(thoiGianNhanCa)); 
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); 
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tongThu = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongThu;
    }

    // Hàm tính tổng thu (Giữ nguyên)
    public double getTongThuDichVuTrongCa(String maNhanVien, LocalDateTime thoiGianNhanCa) {
        double tongThu = 0;
        String sql = "SELECT SUM(ISNULL(ct.soLuong * d.giaTien, 0)) " +
                     "FROM ChiTietHoaDon ct " +
                     "JOIN DichVu d ON ct.maDichVu = d.maDichVu " +
                     "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                     "WHERE hd.maNhanVien = ? AND hd.ngayLap >= ? AND hd.ngayLap < ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setTimestamp(2, Timestamp.valueOf(thoiGianNhanCa)); 
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); 
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tongThu = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongThu;
    }
}