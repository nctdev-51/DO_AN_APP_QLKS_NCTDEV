package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhongDTO {
    private String maPhieu;
    private String maKhachHang;
    private String maPhong;
    private LocalDate ngayDat;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private double tongTien;
    private String trangThai;
    private String maNhanVien;
}