package iuh.fit.core.dto;

import java.time.LocalDate;

public class HoaDonDTO {
    private String maHoaDon;
    private String maNhanVien;
    private String maKhachHang;
    private LocalDate ngayLap;
    private double thueVAT;
    private String maKhuyenMai;
    private String maPhongDat;
    private String ghiChu;
    private double tongTien;

    public HoaDonDTO() {
    }

    public HoaDonDTO(String maHoaDon, String maNhanVien, String maKhachHang, LocalDate ngayLap,
                     double thueVAT, String maKhuyenMai, String maPhongDat, String ghiChu, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.maNhanVien = maNhanVien;
        this.maKhachHang = maKhachHang;
        this.ngayLap = ngayLap;
        this.thueVAT = thueVAT;
        this.maKhuyenMai = maKhuyenMai;
        this.maPhongDat = maPhongDat;
        this.ghiChu = ghiChu;
        this.tongTien = tongTien;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    public double getThueVAT() { return thueVAT; }
    public void setThueVAT(double thueVAT) { this.thueVAT = thueVAT; }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getMaPhongDat() { return maPhongDat; }
    public void setMaPhongDat(String maPhongDat) { this.maPhongDat = maPhongDat; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}