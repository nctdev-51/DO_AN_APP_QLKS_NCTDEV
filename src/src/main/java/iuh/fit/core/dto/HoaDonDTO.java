package iuh.fit.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDTO {
    private String maHoaDon;
    private String maNhanVien;
    private String maKhachHang;
    private String tenKhachHang;
    private LocalDate ngayLap;
    private double thueVAT;
    private String maKhuyenMai;
    private String maPhongDat;
    private String tenPhong;      // FIX: xóa khai báo trùng, chỉ giữ 1 trường
    private String ghiChu;
    private double tongTienPhong;
    private double tongTienDichVu;
    private double chietKhau;
    private double tongTien;
    private String trangThaiThanhToan;
    private List<ChiTietHoaDonDTO> chiTietHoaDons = new ArrayList<>();

    public HoaDonDTO() {
    }

    public HoaDonDTO(String maHoaDon, String maKhachHang, LocalDate ngayLap, double tongTien, String trangThaiThanhToan) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    public double getThueVAT() { return thueVAT; }
    public void setThueVAT(double thueVAT) { this.thueVAT = thueVAT; }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getMaPhongDat() { return maPhongDat; }
    public void setMaPhongDat(String maPhongDat) { this.maPhongDat = maPhongDat; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public double getTongTienPhong() { return tongTienPhong; }
    public void setTongTienPhong(double tongTienPhong) { this.tongTienPhong = tongTienPhong; }

    public double getTongTienDichVu() { return tongTienDichVu; }
    public void setTongTienDichVu(double tongTienDichVu) { this.tongTienDichVu = tongTienDichVu; }

    public double getChietKhau() { return chietKhau; }
    public void setChietKhau(double chietKhau) { this.chietKhau = chietKhau; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; }

    public List<ChiTietHoaDonDTO> getChiTietHoaDons() { return chiTietHoaDons; }
    public void setChiTietHoaDons(List<ChiTietHoaDonDTO> chiTietHoaDons) { this.chiTietHoaDons = chiTietHoaDons; }
}
