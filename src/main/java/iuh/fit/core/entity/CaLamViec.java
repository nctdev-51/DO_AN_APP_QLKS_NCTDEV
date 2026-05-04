package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Entity: CaLamViec (Ca Làm Việc)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Ca Làm Việc
 */
@Entity
@Table(name = "ca_lam_viec")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViec {
    
    @Id
    @Column(name = "ma_ca", length = 20)
    private String maCa;
    
    @Column(name = "ten_ca", length = 100)
    private String tenCa;
    
    @Column(name = "gio_bat_dau")
    private LocalTime gioBatDau;
    
    @Column(name = "gio_ket_thuc")
    private LocalTime gioKetThuc;
}

