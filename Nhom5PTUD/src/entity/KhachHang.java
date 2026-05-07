package entity;

import java.time.LocalDate;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private LoaiKhachHang loaiKhachHang;
    
    public KhachHang() {
        this("", "", "", LocalDate.now(), LoaiKhachHang.KHACH_HOI_VIEN);
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, LocalDate ngaySinh,
                     LoaiKhachHang loaiKhachHang) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.loaiKhachHang = loaiKhachHang;
    }

    // Getters
    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public LoaiKhachHang getLoaiKhachHang() {
        return loaiKhachHang;
    }

    // Setters
    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setLoaiKhachHang(LoaiKhachHang loaiKhachHang) {
        this.loaiKhachHang = loaiKhachHang;
    }
    
    @Override
    public String toString() {
        return String.format("KhachHang[ma=%s, hoTen=%s, SDT=%s, ngaySinh=%s, loai=%s]",
                maKhachHang, hoTen, soDienThoai, ngaySinh, loaiKhachHang);
    }
}
