package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO: NhanVienDTO (Data Transfer Object cho Nhân Viên)
 * 
 * Tầng: CORE - DTO Layer
 * Trách nhiệm: Truyền dữ liệu Nhân Viên giữa các tầng, không lộ Entity trực tiếp
 * 
 * DAO là cầu nối giữa:
 * - PRESENTATION → Gửi NhanVienDTO qua API
 * - SERVICE → Xử lý logic dùng DTO
 * - INFRASTRUCTURE → Mapper chuyển DTO ↔ Entity
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

