package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuCaLamViecDTO {
    private String maLichSu;
    private String maNhanVien;
    private String hoTenNhanVien;
    private String maCa;
    private String tenCa;
    private LocalDateTime thoiGianNhanCa;
    private LocalDateTime thoiGianGiaoCa;
    private double tienDauCa;
    private double tongThuTrongCa;
    private double tienCuoiCaThucTe;
    private double tienChenhLech;
    private String ghiChu;
    private String trangThai;
}