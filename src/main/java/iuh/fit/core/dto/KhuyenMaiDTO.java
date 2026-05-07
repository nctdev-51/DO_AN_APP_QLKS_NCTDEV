package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: KhuyenMaiDTO
 * Mô tả: Data Transfer Object cho Khuyến mại
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String loaiKhuyenMai;
    private double chietKhau;
}

