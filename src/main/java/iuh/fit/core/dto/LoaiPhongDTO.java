package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: LoaiPhongDTO
 * Mô tả: Data Transfer Object cho Loại phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhongDTO {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private String moTa;
}

