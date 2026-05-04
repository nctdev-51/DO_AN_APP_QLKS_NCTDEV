package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: PhieuDatPhong (Phiếu Đặt Phòng)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Phiếu Đặt Phòng
 */
@Entity
@Table(name = "phieu_dat_phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {
    
    @Id
    @Column(name = "ma_phieu_dat", length = 20)
    private String maPhieuDat;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_khach_hang", referencedColumnName = "ma_khach_hang")
    private KhachHang khachHang;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phong", referencedColumnName = "ma_phong")
    private Phong phong;
    
    @Column(name = "ngay_dat")
    private LocalDate ngayDat;
    
    @Column(name = "ngay_nhan_phong")
    private LocalDate ngayNhanPhong;
    
    @Column(name = "ngay_tra_phong")
    private LocalDate ngayTraPhong;
    
    @Column(name = "tong_tien")
    private double tongTien;
}

