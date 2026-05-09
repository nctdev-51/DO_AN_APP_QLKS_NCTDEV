package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO: KhuyenMaiDTO
 * Mô tả: Data Transfer Object cho Khuyến mại
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String loaiKhuyenMai;
    private double chietKhau;
}

