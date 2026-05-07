package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: KhachHangDTO
 * Mô tả: Data Transfer Object cho Khách hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String loaiKhachHang;

    // ✅ Thêm mới: Đối tượng khách (VIP/Thường) để phục vụ logic tính khuyến mãi
    private String doiTuongKhach;
}