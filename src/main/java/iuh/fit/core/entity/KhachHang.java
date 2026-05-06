package iuh.fit.core.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @Column(name = "maKhachHang", length = 5)
    private String maKhachHang;

    @Column(name = "hoTen", length = 50, nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 10, unique = true)
    private String soDienThoai;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Column(name = "loaiKhachHang", length = 50)
    private String loaiKhachHang;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String hoTen, String soDienThoai, LocalDate ngaySinh, String loaiKhachHang) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.loaiKhachHang = loaiKhachHang;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getLoaiKhachHang() {
        return loaiKhachHang;
    }

    public void setLoaiKhachHang(String loaiKhachHang) {
        this.loaiKhachHang = loaiKhachHang;
    }
}