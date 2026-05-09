package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maHoaDon;
    private String maNhanVien;
    private String hoTenNhanVien;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayLap;
    private double thueVAT;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String maPhongDat;
    private String tenPhong;
    private String ghiChu;
    private double tongTien;
    // Thêm các trường còn thiếu
    private double tongTienPhong;
    private double tongTienDichVu;
    private String trangThaiThanhToan;
    private double chietKhau;
    private String maPhieu;
}