package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: DichVu (Dịch Vụ)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Dịch Vụ
 */
@Entity
@Table(name = "dich_vu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {
    
    @Id
    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;
    
    @Column(name = "ten_dich_vu", length = 100, nullable = false)
    private String tenDichVu;
    
    @Column(name = "gia_dich_vu")
    private double giaDichVu;
}

