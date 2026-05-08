package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "PhieuDatPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {
    @Id
    @Column(name = "maPhieu", length = 30)
    private String maPhieu;

    @Column(name = "maKhachHang", length = 30, insertable = false, updatable = false)
    private String maKhachHang;

    @Column(name = "maPhong", length = 4, insertable = false, updatable = false)
    private String maPhong;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @Column(name = "ngayDat")
    private LocalDate ngayDat;

    @Column(name = "ngayNhan")
    private LocalDate ngayNhan;

    @Column(name = "ngayTra")
    private LocalDate ngayTra;

    @Column(name = "tongTien")
    private Double tongTien;

    @Column(name = "trangThai", length = 30, nullable = false)
    private String trangThai = "CHO_NHAN_PHONG";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhong")
    private Phong phong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    // THÊM: OneToMany relationship với ChiTietPhieuDatPhong
    @OneToMany(mappedBy = "phieuDatPhong", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietPhieuDatPhong> dsChiTietPhieuDatPhong = new ArrayList<>();
}