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
    @Column(name = "maKhachHang", length = 30)
    private String maKhachHang;

    @Column(name = "hoTen", length = 50)
    private String hoTen;

    @Column(name = "soDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Column(name = "loaiKhachHang", length = 50)
    private String loaiKhachHang;
}