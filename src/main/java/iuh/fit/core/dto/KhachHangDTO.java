package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String loaiKhachHang;
}