package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "khuyen_mai")
public class KhuyenMai {

    @Id
    @Column(name = "ma_khuyen_mai", length = 20)
    private String maKhuyenMai;

    @Column(name = "ten_khuyen_mai", length = 100)
    private String tenKhuyenMai;

    @Column(name = "muc_giam_gia")
    private double mucGiamGia;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, double mucGiamGia) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.mucGiamGia = mucGiamGia;
    }

    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }

    public String getTenKhuyenMai() { return tenKhuyenMai; }
    public void setTenKhuyenMai(String tenKhuyenMai) { this.tenKhuyenMai = tenKhuyenMai; }

    public double getMucGiamGia() { return mucGiamGia; }
    public void setMucGiamGia(double mucGiamGia) { this.mucGiamGia = mucGiamGia; }
}