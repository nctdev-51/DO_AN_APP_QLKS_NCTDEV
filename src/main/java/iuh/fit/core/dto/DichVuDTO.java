package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVuDTO {
    private String maDichVu;
    private String tenDichVu;
    private double giaTien;
    private String moTa;
}