package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime ngayNhan;
    private LocalDateTime ngayTra;
    private Double tongTien;
    private String trangThai;
    private String maNhanVien;
    private String hoTenNhanVien; // Thêm để hiển thị trên UI

    // ✅ Các trường theo dõi thanh toán / Đặt cọc
    private Double tienCoc;       // Số tiền khách đã cọc trước
    private String loaiThanhToan; // Phương thức: TIEN_MAT, CHUYEN_KHOAN, GHI_NO (Trả sau)

    public Double getTienConNo() {
        double tong = (tongTien != null) ? tongTien : 0.0;
        double coc = (tienCoc != null) ? tienCoc : 0.0;
        return tong - coc;
    }
}