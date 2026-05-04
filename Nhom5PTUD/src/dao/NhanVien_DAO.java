package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.LoaiNhanVien;
import entity.NhanVien;

public class NhanVien_DAO {

    // 1. Giữ kết nối là một biến thành viên (instance variable)
    private Connection con;

    // 2. Khởi tạo kết nối trong constructor
    public NhanVien_DAO() {
        con = ConnectDB.getInstance().getConnection();
    }

    // 3. Hàm thêm (Đã sửa lại, không đóng connection)
    public boolean themNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, ngaySinh, gioiTinh, CCCD, soDienThoai, trangThai, loaiNhanVien, ngayVaoLam, queQuan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Không dùng try-with-resources cho 'con'
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHoTen());
            
            // 4. Xử lý NULL cho Date
            if (nv.getNgaySinh() != null) {
                ps.setDate(3, Date.valueOf(nv.getNgaySinh()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            
            ps.setBoolean(4, nv.isGioiTinh());
            ps.setString(5, nv.getCCCD());
            ps.setString(6, nv.getSoDienThoai());
            ps.setBoolean(7, nv.isTrangThai());
            ps.setString(8, nv.getLoaiNhanVien().name()); // Dùng .name() để lấy chuỗi "NHAN_VIEN_LE_TAN"
            
            if (nv.getNgayVaoLam() != null) {
                ps.setDate(9, Date.valueOf(nv.getNgayVaoLam()));
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }
            
            ps.setString(10, nv.getQueQuan());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Hàm cập nhật (Đã sửa lại, không đóng connection và xử lý NULL)
    public boolean capNhatNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET hoTen=?, ngaySinh=?, gioiTinh=?, CCCD=?, soDienThoai=?, " +
                     "trangThai=?, loaiNhanVien=?, ngayVaoLam=?, queQuan=? WHERE maNhanVien=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            
            if (nv.getNgaySinh() != null) {
                ps.setDate(2, Date.valueOf(nv.getNgaySinh()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            
            ps.setBoolean(3, nv.isGioiTinh());
            ps.setString(4, nv.getCCCD());
            ps.setString(5, nv.getSoDienThoai());
            ps.setBoolean(6, nv.isTrangThai());
            ps.setString(7, nv.getLoaiNhanVien().name());
            
            if (nv.getNgayVaoLam() != null) {
                ps.setDate(8, Date.valueOf(nv.getNgayVaoLam()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            
            ps.setString(9, nv.getQueQuan());
            ps.setString(10, nv.getMaNhanVien());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Hàm xóa (Đã sửa lại, không đóng connection)
    public boolean xoaNhanVien(String maNV) {
        // Lưu ý: Tốt hơn nên là cập nhật trangThai = 0 thay vì xóa cứng
        String sql = "DELETE FROM NhanVien WHERE maNhanVien=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7. Hàm helper để parse ResultSet, tránh lặp code và xử lý NULL
    private NhanVien parseNhanVien(ResultSet rs) throws SQLException {
        String maNV = rs.getString("maNhanVien");
        String hoTen = rs.getString("hoTen");
        
        // Xử lý Date NULL
        Date sqlNgaySinh = rs.getDate("ngaySinh");
        Date sqlNgayVaoLam = rs.getDate("ngayVaoLam");
        
        return new NhanVien(
            maNV,
            hoTen,
            sqlNgaySinh != null ? sqlNgaySinh.toLocalDate() : null, // Fix NullPointer
            rs.getBoolean("gioiTinh"),
            rs.getString("CCCD"),
            rs.getString("soDienThoai"),
            rs.getBoolean("trangThai"),
            LoaiNhanVien.fromString(rs.getString("loaiNhanVien")), // Dùng fromString() an toàn hơn valueOf()
            sqlNgayVaoLam != null ? sqlNgayVaoLam.toLocalDate() : null, // Fix NullPointer
            rs.getString("queQuan")
        );
    }

    /**
     * Lấy nhân viên theo mã.
     * (Hợp nhất từ hàm timTheoMa của bạn và getNhanVienByMa tôi đã dùng)
     */
    public NhanVien getNhanVienByMa(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return parseNhanVien(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy TẤT CẢ nhân viên (bao gồm cả nhân viên đã nghỉ).
     */
    public List<NhanVien> getAllNhanVien_TatCa() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(parseNhanVien(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    /**
     * Lấy danh sách nhân viên ĐANG LÀM VIỆC (trangThai = 1).
     * Dùng cho các bảng GUI và ComboBox.
     */
    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE trangThai = 1";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsNV.add(parseNhanVien(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }
}