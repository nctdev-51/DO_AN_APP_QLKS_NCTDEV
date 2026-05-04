package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import entity.CaLamViec;

public class CaLamViec_DAO {
    private Connection con;

    public CaLamViec_DAO() {
        con = ConnectDB.getInstance().getConnection();
    }

    public List<CaLamViec> getAllCaLamViec() {
        List<CaLamViec> dsCa = new ArrayList<>();
        // Bỏ qua cột 'ngay' vì nó không hợp lý trong bảng mẫu ca
        String sql = "SELECT maCa, tenCa, thoiGianBatDau, thoiGianKetThuc, ghiChu, trangThai FROM CaLamViec";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                CaLamViec ca = new CaLamViec(
                    rs.getString("maCa"),
                    rs.getString("tenCa"),
                    rs.getTime("thoiGianBatDau") != null ? rs.getTime("thoiGianBatDau").toLocalTime() : null,
                    rs.getTime("thoiGianKetThuc") != null ? rs.getTime("thoiGianKetThuc").toLocalTime() : null,
                    rs.getString("ghiChu"),
                    rs.getBoolean("trangThai")
                );
                dsCa.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCa;
    }
    
    public CaLamViec getCaLamViecByMa(String maCa) {
        CaLamViec ca = null;
        String sql = "SELECT maCa, tenCa, thoiGianBatDau, thoiGianKetThuc, ghiChu, trangThai FROM CaLamViec WHERE maCa = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maCa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ca = new CaLamViec(
                    rs.getString("maCa"),
                    rs.getString("tenCa"),
                    rs.getTime("thoiGianBatDau") != null ? rs.getTime("thoiGianBatDau").toLocalTime() : null,
                    rs.getTime("thoiGianKetThuc") != null ? rs.getTime("thoiGianKetThuc").toLocalTime() : null,
                    rs.getString("ghiChu"),
                    rs.getBoolean("trangThai")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ca;
    }

    public boolean addCaLamViec(CaLamViec ca) {
        String sql = "INSERT INTO CaLamViec (maCa, tenCa, thoiGianBatDau, thoiGianKetThuc, ghiChu, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getMaCa());
            stmt.setString(2, ca.getTenCa());
            stmt.setTime(3, Time.valueOf(ca.getThoiGianBatDau()));
            stmt.setTime(4, Time.valueOf(ca.getThoiGianKetThuc()));
            stmt.setString(5, ca.getGhiChu());
            stmt.setBoolean(6, ca.isTrangThai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCaLamViec(CaLamViec ca) {
        String sql = "UPDATE CaLamViec SET tenCa = ?, thoiGianBatDau = ?, thoiGianKetThuc = ?, ghiChu = ?, trangThai = ? WHERE maCa = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ca.getTenCa());
            stmt.setTime(2, Time.valueOf(ca.getThoiGianBatDau()));
            stmt.setTime(3, Time.valueOf(ca.getThoiGianKetThuc()));
            stmt.setString(4, ca.getGhiChu());
            stmt.setBoolean(5, ca.isTrangThai());
            stmt.setString(6, ca.getMaCa());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCaLamViec(String maCa) {
        String sql = "DELETE FROM CaLamViec WHERE maCa = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maCa);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}