package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: KhachHang (Khách hàng)
 * Bảng: KhachHang
 * Mô tả: Lưu trữ thông tin chi tiết của các khách hàng khách sạn
 */
@Entity
@Table(name = "KhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @Column(name = "maKhachHang", length = 5)
    private String maKhachHang;

    @Column(name = "hoTen", length = 50, nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 10, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "loaiKhachHang", length = 50, nullable = false)
    private String loaiKhachHang; // 'KHACH_VANG_LAI' hoặc 'KHACH_HOI_VIEN'
}