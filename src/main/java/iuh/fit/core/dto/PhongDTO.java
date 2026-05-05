package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhongDTO {
    private String maPhong;
    private String tenPhong;
    private String maLoaiPhong;
    private double giaPhong;
    private String tinhTrang;
}