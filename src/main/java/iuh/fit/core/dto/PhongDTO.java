package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: PhongDTO
 * Mô tả: Data Transfer Object cho Phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhongDTO {
    private String maPhong;
    private String tenPhong;
    private String maLoaiPhong;
    private String tenLoaiPhong; // Thêm để hiển thị trên UI
    private double giaPhong;
    private String tinhTrang;
}