package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: KhachHangDTO (Data Transfer Object cho Khách Hàng)
 * 
 * Tầng: CORE - DTO Layer
 * Trách nhiệm: Truyền dữ liệu Khách Hàng giữa các tầng
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
}

