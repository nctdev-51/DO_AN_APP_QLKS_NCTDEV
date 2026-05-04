package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: HoaDon (Hóa Đơn)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Hóa Đơn
 */
@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    
    @Id
    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phieu_dat", referencedColumnName = "ma_phieu_dat")
    private PhieuDatPhong phieuDatPhong;
    
    @Column(name = "tong_tien")
    private double tongTien;
    
    @Column(name = "trang_thai_thanh_toan")
    private String trangThaiThanhToan; // "Chưa thanh toán", "Đã thanh toán"
}

