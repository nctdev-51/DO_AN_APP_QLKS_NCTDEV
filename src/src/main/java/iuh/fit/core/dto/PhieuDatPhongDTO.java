package iuh.fit.core.dto;

import java.time.LocalDate;

public class PhieuDatPhongDTO {
    private String maPhieu;
    private String maKhachHang;
    private String maPhong;
    private LocalDate ngayDat;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private double tongTien;
    private String trangThai;
    private String maNhanVien;
    private String tenKhachHang;  // join từ KhachHang
    private String tenPhong;      // join từ Phong

    public PhieuDatPhongDTO() {
    }

    public PhieuDatPhongDTO(String maPhieu, String maKhachHang, String maPhong, LocalDate ngayDat,
                            LocalDate ngayNhan, LocalDate ngayTra, double tongTien, String trangThai, String maNhanVien) {
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

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    // FIX: thêm getter/setter cho tenKhachHang và tenPhong (trước đây thiếu)
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
}
