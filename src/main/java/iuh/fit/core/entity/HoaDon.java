package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phieu_dat")
    private PhieuDatPhong phieuDatPhong;

    @Column(name = "tong_tien")
    private double tongTien;

    @Column(name = "trang_thai_thanh_toan")
    private String trangThaiThanhToan;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, PhieuDatPhong phieuDatPhong, double tongTien, String trangThaiThanhToan) {
        this.maHoaDon = maHoaDon;
        this.phieuDatPhong = phieuDatPhong;
        this.tongTien = tongTien;
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; }
}