package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: HoaDon (Hóa đơn)
 * Bảng: HoaDon
 * Mô tả: Lưu trữ thông tin hóa đơn thanh toán của khách hàng
 */
@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @Column(name = "maHoaDon", length = 10)
    private String maHoaDon;

    @Column(name = "ngayLap")
    private LocalDate ngayLap;

    @Column(name = "thueVAT")
    private double thueVAT;


    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    @Column(name = "tongTien")
    private double tongTien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhuyenMai")
    private KhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhongDat")
    private Phong phong;
}