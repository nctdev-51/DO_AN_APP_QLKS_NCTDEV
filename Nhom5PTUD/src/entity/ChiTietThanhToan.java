package entity;

import java.util.Objects; // <<< Thêm import này

public class ChiTietThanhToan {
    
    // Khóa ngoại
    private HoaDon hoaDon; // <<< Đổi từ String sang HoaDon
    private HinhThucThanhToan hinhThuc;
    
    // Thuộc tính riêng
    private double soTien;
    private String maGiaoDich; // Mã giao dịch ngân hàng (nếu có)
    
    /**
     * Dùng khi tạo mới một chi tiết thanh toán (chưa có Hóa Đơn)
     */
    public ChiTietThanhToan(HinhThucThanhToan hinhThuc, double soTien, String maGiaoDich) {
        this.hinhThuc = hinhThuc;
        this.soTien = soTien;
        this.maGiaoDich = maGiaoDich;
    }

    /**
     * Dùng khi tải từ CSDL lên
     */
    public ChiTietThanhToan(HoaDon hoaDon, HinhThucThanhToan hinhThuc, double soTien, String maGiaoDich) {
        this.hoaDon = hoaDon;
        this.hinhThuc = hinhThuc;
        this.soTien = soTien;
        this.maGiaoDich = maGiaoDich;
    }

    // --- Getters & Setters ---
    
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    // <<< THAY ĐỔI: Hàm setHoaDon (nhận đối tượng) >>>
    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public HinhThucThanhToan getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(HinhThucThanhToan hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }
    
    // <<< THÊM hashCode và equals (Rất quan trọng cho List/Map) >>>
    @Override
    public int hashCode() {
        // Khóa chính là HoaDon và HinhThucThanhToan
        return Objects.hash(hinhThuc, hoaDon);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChiTietThanhToan other = (ChiTietThanhToan) obj;
        return Objects.equals(hinhThuc, other.hinhThuc) && Objects.equals(hoaDon, other.hoaDon);
    }
}