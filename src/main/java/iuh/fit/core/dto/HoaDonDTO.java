package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {
    private String maHoaDon;
    private String maNhanVien;
    private String maKhachHang;
    private LocalDate ngayLap;
    private double thueVAT;
    private String maKhuyenMai;
    private String maPhongDat;
    private String ghiChu;
    private double tongTien;
}