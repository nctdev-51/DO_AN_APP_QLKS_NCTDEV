package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: KhachHang (Khách Hàng)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Khách Hàng với các thuộc tính cơ bản
 */
@Entity
@Table(name = "khach_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    
    @Id
    @Column(name = "ma_khach_hang", length = 20)
    private String maKhachHang;
    
    @Column(name = "ho_ten", length = 100, nullable = false)
    private String hoTen;
    
    @Column(name = "so_dien_thoai", length = 20, unique = true)
    private String soDienThoai;
    
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_khach_hang")
    private LoaiKhachHang loaiKhachHang;
}

