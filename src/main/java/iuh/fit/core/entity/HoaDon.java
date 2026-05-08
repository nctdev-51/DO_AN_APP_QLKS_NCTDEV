package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @Column(name = "maHoaDon", length = 30) // Đã cập nhật length = 30
    private String maHoaDon;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @Column(name = "maKhachHang", length = 30, insertable = false, updatable = false)
    private String maKhachHang;

    @Column(name = "ngayLap")
    private LocalDate ngayLap;

    @Column(name = "thueVAT")
    private double thueVAT;

    @Column(name = "maKhuyenMai", length = 5, insertable = false, updatable = false)
    private String maKhuyenMai;

    @Column(name = "maPhongDat", length = 4, insertable = false, updatable = false)
    private String maPhongDat;

    @Column(name = "tenPhong", length = 100)
    private String tenPhong;

    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    @Column(name = "tongTienPhong")
    private double tongTienPhong;

    @Column(name = "tongTienDichVu")
    private double tongTienDichVu;

    @Column(name = "chietKhau")
    private double chietKhau;

    @Column(name = "tongTien")
    private double tongTien;

    @Column(name = "trangThaiThanhToan", length = 50)
    private String trangThaiThanhToan;

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

    // THÊM: OneToMany relationship với ChiTietHoaDon
    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietHoaDon> dsChiTietHoaDon = new ArrayList<>();
}