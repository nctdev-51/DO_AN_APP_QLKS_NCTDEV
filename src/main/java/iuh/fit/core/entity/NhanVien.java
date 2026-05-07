package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: NhanVien (Nhân viên)
 * Bảng: NhanVien
 * Mô tả: Lưu trữ thông tin chi tiết của các nhân viên khách sạn
 */
@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @Column(name = "maNhanVien", length = 5)
    private String maNhanVien;

    @Column(name = "hoTen", length = 50, nullable = false)
    private String hoTen;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "gioiTinh", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean gioiTinh;

    @Column(name = "CCCD", length = 12, nullable = false, unique = true)
    private String cccd;

    @Column(name = "soDienThoai", length = 15, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "trangThai", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean trangThai;

    @Column(name = "loaiNhanVien", length = 50, nullable = false)
    private String loaiNhanVien; // 'NHAN_VIEN_LE_TAN' hoặc 'NHAN_VIEN_QUAN_LY'

    @Column(name = "ngayVaoLam", nullable = false)
    private LocalDate ngayVaoLam;

    @Column(name = "queQuan", length = 50, nullable = false)
    private String queQuan;
}