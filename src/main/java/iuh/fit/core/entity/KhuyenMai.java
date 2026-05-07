package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: KhuyenMai (Khuyến mại / Giảm giá)
 * Bảng: KhuyenMai
 * Mô tả: Lưu trữ thông tin các chương trình khuyến mại (giảm giá theo khách hàng hoặc phòng)
 */
@Entity
@Table(name = "KhuyenMai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    @Id
    @Column(name = "maKhuyenMai", length = 5)
    private String maKhuyenMai;

    @Column(name = "tenKhuyenMai", length = 50, nullable = false)
    private String tenKhuyenMai;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "loaiKhuyenMai", length = 50, nullable = false)
    private String loaiKhuyenMai; // 'THEO_KHACH_HANG' hoặc 'THEO_PHONG'

    @Column(name = "chietKhau", nullable = false)
    private double chietKhau;
}