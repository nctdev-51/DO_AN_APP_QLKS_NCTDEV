package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: PhieuDatPhongDTO
 * Mô tả: Data Transfer Object cho Phiếu đặt phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhongDTO {
    private String maPhieu;
    private String maKhachHang;
    private String tenKhachHang; // Thêm để hiển thị trên UI
    private String maPhong;
    private String tenPhong; // Thêm để hiển thị trên UI
    private LocalDate ngayDat;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private Double tongTien;
    private String trangThai;
    private String maNhanVien;
    private String hoTenNhanVien; // Thêm để hiển thị trên UI

    // ✅ Thêm mới: Các trường theo dõi thanh toán
    private String loaiThanhToan; // Ví dụ: TIEN_MAT, CHUYEN_KHOAN, THE_TIN_DUNG
    private Double tienTamUng;    // Số tiền khách đã trả trước/cọc
    private Double tienConNo;     // Số tiền còn lại phải thanh toán
}