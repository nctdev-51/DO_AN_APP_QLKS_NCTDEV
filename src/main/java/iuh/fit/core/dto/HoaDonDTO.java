package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: HoaDonDTO
 * Mô tả: Data Transfer Object cho Hóa đơn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {
    private String maHoaDon;
    private String maNhanVien;
    private String hoTenNhanVien; // Thêm để hiển thị trên UI
    private String maKhachHang;
    private String tenKhachHang; // Thêm để hiển thị trên UI
    private LocalDate ngayLap;
    private double thueVAT;
    private String maKhuyenMai;
    private String tenKhuyenMai; // Thêm để hiển thị trên UI
    private String maPhongDat;
    private String tenPhong; // Thêm để hiển thị trên UI
    private String ghiChu;
    private double tongTien;
}