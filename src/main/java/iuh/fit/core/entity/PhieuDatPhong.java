package iuh.fit.core.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PhieuDatPhong")
public class PhieuDatPhong {

    @Id
    @Column(name = "maPhieu", length = 10)
    private String maPhieu;

    @Column(name = "maKhachHang", length = 5)
    private String maKhachHang;

    @Column(name = "maPhong", length = 4)
    private String maPhong;

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

    @Column(name = "maNhanVien", length = 5)
    private String maNhanVien;

    public PhieuDatPhong() {
    }

    public PhieuDatPhong(String maPhieu, String maKhachHang, String maPhong, LocalDate ngayDat,
                         LocalDate ngayNhan, LocalDate ngayTra, Double tongTien, String trangThai, String maNhanVien) {
        this.maPhieu = maPhieu;
        this.maKhachHang = maKhachHang;
        this.maPhong = maPhong;
        this.ngayDat = ngayDat;
        this.ngayNhan = ngayNhan;
        this.ngayTra = ngayTra;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.maNhanVien = maNhanVien;
    }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public LocalDate getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDate ngayDat) { this.ngayDat = ngayDat; }

    public LocalDate getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDate ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDate getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDate ngayTra) { this.ngayTra = ngayTra; }

    public Double getTongTien() { return tongTien; }
    public void setTongTien(Double tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
}