package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: PhieuDatPhong (Phiếu đặt phòng)
 * Bảng: PhieuDatPhong
 * Mô tả: Lưu trữ thông tin các phiếu đặt phòng của khách hàng
 */
@Entity
@Table(name = "PhieuDatPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {
    @Id
    @Column(name = "maPhieu", length = 10)
    private String maPhieu;

    @Column(name = "ngayDat")
    private LocalDate ngayDat;

    @Column(name = "ngayNhan")
    private LocalDate ngayNhan;

    @Column(name = "ngayTra")
    private LocalDate ngayTra;

    @Column(name = "tongTien")
    private Double tongTien;

    @Column(name = "trangThai", length = 30, nullable = false)
    private String trangThai; // 'CHO_NHAN_PHONG', 'DA_NHAN_PHONG', 'DA_TRA_PHONG', 'DA_HUY'


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhong")
    private Phong phong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;
}