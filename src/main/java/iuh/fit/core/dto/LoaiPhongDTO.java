package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO: LoaiPhongDTO
 * Mô tả: Data Transfer Object cho Loại phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhongDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private String moTa;
}

