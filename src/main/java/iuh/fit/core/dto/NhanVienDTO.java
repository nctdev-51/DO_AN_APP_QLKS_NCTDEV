package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: NhanVienDTO
 * Mô tả: Data Transfer Object cho Nhân viên, dùng để truyền dữ liệu giữa các tầng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDTO {
    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private boolean gioiTinh;
    private String cccd;
    private String soDienThoai;
    private boolean trangThai;
    private String loaiNhanVien;
    private LocalDate ngayVaoLam;
    private String queQuan;
}