package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO: DichVuDTO
 * Mô tả: Data Transfer Object cho Dịch vụ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maDichVu;
    private String tenDichVu;
    private double giaTien;
    private String moTa;
}