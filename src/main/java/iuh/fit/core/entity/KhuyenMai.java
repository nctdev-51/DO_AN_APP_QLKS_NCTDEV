package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: KhuyenMai (Khuyến Mãi / Promotion)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Khuyến Mãi
 */
@Entity
@Table(name = "khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    
    @Id
    @Column(name = "ma_khuyen_mai", length = 20)
    private String maKhuyenMai;
    
    @Column(name = "ten_khuyen_mai", length = 100)
    private String tenKhuyenMai;
    
    @Column(name = "muc_giam_gia")
    private double mucGiamGia; // Percentage (0-100)
}

