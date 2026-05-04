package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon {
    private String maHoaDon;
    private PhieuDatPhong phieuDatPhong; // Liên kết 1-1 với PhieuDatPhong
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private LocalDate ngayLap;
    private double thueVAT;
    private KhuyenMai khuyenMai;
    private String ghiChu;
    private double tongTien; // Tổng tiền cuối cùng
    
    // Danh sách các hình thức đã thanh toán
    private List<ChiTietThanhToan> dsChiTietThanhToan;

    public HoaDon() {
        this.dsChiTietThanhToan = new ArrayList<>();
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
        this.dsChiTietThanhToan = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, PhieuDatPhong phieuDatPhong, NhanVien nhanVien, KhachHang khachHang,
            LocalDate ngayLap, double thueVAT, KhuyenMai khuyenMai, String ghiChu, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.phieuDatPhong = phieuDatPhong;
        this.nhanVien = nhanVien;
        this.khachHang = khachHang;
        this.ngayLap = ngayLap;
        this.thueVAT = thueVAT;
        this.khuyenMai = khuyenMai;
        this.ghiChu = ghiChu;
        this.tongTien = tongTien;
        this.dsChiTietThanhToan = new ArrayList<>();
    }
    
    // --- Getters & Setters ---
    // (Xóa bỏ các hàm helper setMaPhieu, setMaNhanVien, setMaKhachHang)

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public PhieuDatPhong getPhieuDatPhong() {
        return phieuDatPhong;
    }

    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        this.phieuDatPhong = phieuDatPhong;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(double thueVAT) {
        this.thueVAT = thueVAT;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public List<ChiTietThanhToan> getDsChiTietThanhToan() {
        return dsChiTietThanhToan;
    }

    public void setDsChiTietThanhToan(List<ChiTietThanhToan> dsChiTietThanhToan) {
        this.dsChiTietThanhToan = dsChiTietThanhToan;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HoaDon other = (HoaDon) obj;
        return Objects.equals(maHoaDon, other.maHoaDon);
    }
}