package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: Phong (Phòng Khách Sạn)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Phòng
 */
@Entity
@Table(name = "phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    
    @Id
    @Column(name = "ma_phong", length = 20)
    private String maPhong;
    
    @Column(name = "ten_phong", length = 100)
    private String tenPhong;
    
    @Column(name = "so_tang")
    private int soTang;
    
    @Column(name = "suc_chua")
    private int sucChua;
    
    @Column(name = "gia_phong")
    private double giaPhong;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tinh_trang_phong")
    private TinhTrangPhong tinhTrangPhong;
}

