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
    @Column(name = "maHoaDon", length = 30)
    private String maHoaDon;

    @Column(name = "ngayLap")
    private LocalDate ngayLap;

    @Column(name = "thueVAT")
    private double thueVAT;

    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    @Column(name = "tongTien")
    private double tongTien;

    @Column(name = "tongTienPhong")
    private double tongTienPhong;

    @Column(name = "tongTienDichVu")
    private double tongTienDichVu;

    @Column(name = "chietKhau")
    private double chietKhau;

    @Column(name = "trangThaiThanhToan", length = 50)
    private String trangThaiThanhToan;

    @Column(name = "tenPhong", length = 100)
    private String tenPhong;

    @Column(name = "maKhachHang", length = 30, insertable = false, updatable = false)
    private String maKhachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @Column(name = "maKhuyenMai", length = 5, insertable = false, updatable = false)
    private String maKhuyenMai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhuyenMai")
    private KhuyenMai khuyenMai;

    @Column(name = "maPhongDat", length = 4, insertable = false, updatable = false)
    private String maPhongDat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhongDat")
    private Phong phong;
}