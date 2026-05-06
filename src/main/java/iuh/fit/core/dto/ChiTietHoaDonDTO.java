package iuh.fit.core.dto;

public class ChiTietHoaDonDTO {
    private String maHoaDon;
    private String maDichVu;
    private String tenDichVu;
    private int soLuong;
    private double giaTienTungDichVu;
    private double thanhTien;

    public ChiTietHoaDonDTO() {
    }

    public ChiTietHoaDonDTO(String maHoaDon, String maDichVu, String tenDichVu, int soLuong, double giaTienTungDichVu) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.soLuong = soLuong;
        this.giaTienTungDichVu = giaTienTungDichVu;
        this.thanhTien = soLuong * giaTienTungDichVu;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = soLuong * giaTienTungDichVu;
    }

    public double getGiaTienTungDichVu() { return giaTienTungDichVu; }
    public void setGiaTienTungDichVu(double giaTienTungDichVu) {
        this.giaTienTungDichVu = giaTienTungDichVu;
        this.thanhTien = soLuong * giaTienTungDichVu;
    }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}

