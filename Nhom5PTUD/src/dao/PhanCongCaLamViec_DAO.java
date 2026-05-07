package dao;

import entity.PhanCongCaLamViec;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;

public class PhanCongCaLamViec_DAO {
    private Connection connection;

    public PhanCongCaLamViec_DAO() {
        connection = ConnectDB.getInstance().getConnection();
    }

    // Lấy tất cả phân công
    public List<PhanCongCaLamViec> getDanhSachPhanCong() {
        List<PhanCongCaLamViec> dsPhanCong = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen as tenNhanVien, cl.tenCa, cl.thoiGianBatDau, cl.thoiGianKetThuc " +
                     "FROM PhanCongCaLamViec pc " +
                     "INNER JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien " +
                     "INNER JOIN CaLamViec cl ON pc.maCa = cl.maCa " +
                     "ORDER BY pc.ngayLamViec DESC, cl.thoiGianBatDau";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PhanCongCaLamViec pc = new PhanCongCaLamViec(
                    rs.getString("maPhanCong"),
                    rs.getString("maNhanVien"),
                    rs.getString("maCa"),
                    rs.getDate("ngayLamViec"),
                    rs.getString("trangThai"),
                    rs.getTimestamp("thoiGianNhanCa"),
                    rs.getTimestamp("thoiGianBanGiao")
                );
                pc.setTenNhanVien(rs.getString("tenNhanVien"));
                pc.setTenCa(rs.getString("tenCa"));
                pc.setThoiGianBatDau(rs.getTime("thoiGianBatDau"));
                pc.setThoiGianKetThuc(rs.getTime("thoiGianKetThuc"));
                dsPhanCong.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }

    // Lấy phân công theo nhân viên và ngày
    public List<PhanCongCaLamViec> getPhanCongTheoNhanVienVaNgay(String maNhanVien, Date ngayLamViec) {
        List<PhanCongCaLamViec> dsPhanCong = new ArrayList<>();
        String sql = "SELECT pc.*, nv.hoTen as tenNhanVien, cl.tenCa, cl.thoiGianBatDau, cl.thoiGianKetThuc " +
                     "FROM PhanCongCaLamViec pc " +
                     "INNER JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien " +
                     "INNER JOIN CaLamViec cl ON pc.maCa = cl.maCa " +
                     "WHERE pc.maNhanVien = ? AND pc.ngayLamViec = ? " +
                     "ORDER BY cl.thoiGianBatDau";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setDate(2, ngayLamViec);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PhanCongCaLamViec pc = new PhanCongCaLamViec(
                    rs.getString("maPhanCong"),
                    rs.getString("maNhanVien"),
                    rs.getString("maCa"),
                    rs.getDate("ngayLamViec"),
                    rs.getString("trangThai"),
                    rs.getTimestamp("thoiGianNhanCa"),
                    rs.getTimestamp("thoiGianBanGiao")
                );
                pc.setTenNhanVien(rs.getString("tenNhanVien"));
                pc.setTenCa(rs.getString("tenCa"));
                pc.setThoiGianBatDau(rs.getTime("thoiGianBatDau"));
                pc.setThoiGianKetThuc(rs.getTime("thoiGianKetThuc"));
                dsPhanCong.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanCong;
    }

    // Lấy phân công chưa nhận ca của nhân viên trong ngày
    public PhanCongCaLamViec getPhanCongChuaNhanCa(String maNhanVien, Date ngayLamViec) {
        String sql = "SELECT pc.*, nv.hoTen as tenNhanVien, cl.tenCa, cl.thoiGianBatDau, cl.thoiGianKetThuc " +
                     "FROM PhanCongCaLamViec pc " +
                     "INNER JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien " +
                     "INNER JOIN CaLamViec cl ON pc.maCa = cl.maCa " +
                     "WHERE pc.maNhanVien = ? AND pc.ngayLamViec = ? AND pc.trangThai = N'Chưa nhận ca'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setDate(2, ngayLamViec);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PhanCongCaLamViec pc = new PhanCongCaLamViec(
                    rs.getString("maPhanCong"),
                    rs.getString("maNhanVien"),
                    rs.getString("maCa"),
                    rs.getDate("ngayLamViec"),
                    rs.getString("trangThai"),
                    rs.getTimestamp("thoiGianNhanCa"),
                    rs.getTimestamp("thoiGianBanGiao")
                );
                pc.setTenNhanVien(rs.getString("tenNhanVien"));
                pc.setTenCa(rs.getString("tenCa"));
                pc.setThoiGianBatDau(rs.getTime("thoiGianBatDau"));
                pc.setThoiGianKetThuc(rs.getTime("thoiGianKetThuc"));
                return pc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy phân công đang làm việc của nhân viên
    public PhanCongCaLamViec getPhanCongDangLamViec(String maNhanVien, Date ngayLamViec) {
        String sql = "SELECT pc.*, nv.hoTen as tenNhanVien, cl.tenCa, cl.thoiGianBatDau, cl.thoiGianKetThuc " +
                     "FROM PhanCongCaLamViec pc " +
                     "INNER JOIN NhanVien nv ON pc.maNhanVien = nv.maNhanVien " +
                     "INNER JOIN CaLamViec cl ON pc.maCa = cl.maCa " +
                     "WHERE pc.maNhanVien = ? AND pc.ngayLamViec = ? AND pc.trangThai = N'Đang làm việc'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setDate(2, ngayLamViec);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                PhanCongCaLamViec pc = new PhanCongCaLamViec(
                    rs.getString("maPhanCong"),
                    rs.getString("maNhanVien"),
                    rs.getString("maCa"),
                    rs.getDate("ngayLamViec"),
                    rs.getString("trangThai"),
                    rs.getTimestamp("thoiGianNhanCa"),
                    rs.getTimestamp("thoiGianBanGiao")
                );
                pc.setTenNhanVien(rs.getString("tenNhanVien"));
                pc.setTenCa(rs.getString("tenCa"));
                pc.setThoiGianBatDau(rs.getTime("thoiGianBatDau"));
                pc.setThoiGianKetThuc(rs.getTime("thoiGianKetThuc"));
                return pc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm phân công mới
    public boolean themPhanCong(PhanCongCaLamViec phanCong) {
        String sql = "INSERT INTO PhanCongCaLamViec (maPhanCong, maNhanVien, maCa, ngayLamViec, trangThai) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phanCong.getMaPhanCong());
            stmt.setString(2, phanCong.getMaNhanVien());
            stmt.setString(3, phanCong.getMaCa());
            stmt.setDate(4, phanCong.getNgayLamViec());
            stmt.setString(5, phanCong.getTrangThai());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nhận ca
    public boolean nhanCa(String maPhanCong, Timestamp thoiGianNhanCa) {
        String sql = "UPDATE PhanCongCaLamViec SET trangThai = N'Đang làm việc', thoiGianNhanCa = ? WHERE maPhanCong = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, thoiGianNhanCa);
            stmt.setString(2, maPhanCong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Bàn giao ca
    public boolean banGiaoCa(String maPhanCong, Timestamp thoiGianBanGiao) {
        String sql = "UPDATE PhanCongCaLamViec SET trangThai = N'Đã bàn giao', thoiGianBanGiao = ? WHERE maPhanCong = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, thoiGianBanGiao);
            stmt.setString(2, maPhanCong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật phân công
    public boolean capNhatPhanCong(PhanCongCaLamViec phanCong) {
        String sql = "UPDATE PhanCongCaLamViec SET maNhanVien = ?, maCa = ?, ngayLamViec = ? WHERE maPhanCong = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phanCong.getMaNhanVien());
            stmt.setString(2, phanCong.getMaCa());
            stmt.setDate(3, phanCong.getNgayLamViec());
            stmt.setString(4, phanCong.getMaPhanCong());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phân công
    public boolean xoaPhanCong(String maPhanCong) {
        String sql = "DELETE FROM PhanCongCaLamViec WHERE maPhanCong = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maPhanCong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra trùng phân công
    public boolean kiemTraTrungPhanCong(String maNhanVien, String maCa, Date ngayLamViec) {
        String sql = "SELECT COUNT(*) FROM PhanCongCaLamViec WHERE maNhanVien = ? AND maCa = ? AND ngayLamViec = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maNhanVien);
            stmt.setString(2, maCa);
            stmt.setDate(3, ngayLamViec);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}