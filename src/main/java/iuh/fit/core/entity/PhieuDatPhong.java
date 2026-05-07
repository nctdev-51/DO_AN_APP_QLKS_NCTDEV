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
    @Column(name = "maPhieu", length = 30)
    private String maPhieu;

    @Column(name = "maKhachHang", length = 30, insertable = false, updatable = false)
    private String maKhachHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    @Column(name = "maPhong", length = 4, insertable = false, updatable = false)
    private String maPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhong")
    private Phong phong;

    @Column(name = "ngayDat")
    private LocalDate ngayDat;

    @Column(name = "ngayNhan")
    private LocalDate ngayNhan;

    @Column(name = "ngayTra")
    private LocalDate ngayTra;

    @Column(name = "tongTien")
    private Double tongTien;

    @Column(name = "trangThai", length = 30)
    private String trangThai;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;
}