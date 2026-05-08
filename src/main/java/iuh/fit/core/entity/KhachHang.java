package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "KhachHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @Column(name = "maKhachHang", length = 30) // Đã cập nhật length = 30
    private String maKhachHang;

    @Column(name = "hoTen", length = 50, nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 15, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "loaiKhachHang", length = 50, nullable = false)
    private String loaiKhachHang;
}