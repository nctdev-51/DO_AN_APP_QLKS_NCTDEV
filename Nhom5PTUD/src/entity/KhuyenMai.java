package entity;

import java.time.LocalDate;
import java.util.Objects;

// <<< KHÔNG CẦN ĐỊNH NGHĨA enum GIẢ ĐỊNH Ở ĐÂY NỮA >>>
// (Vì nó đã được import từ entity.LoaiKhuyenMai)

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private LoaiKhuyenMai loaiKhuyenMai; // <<< Sử dụng tệp enum của bạn
    private double chietKhau;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDate ngayBatDau, LocalDate ngayKetThuc,
            LoaiKhuyenMai loaiKhuyenMai, double chietKhau) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.loaiKhuyenMai = loaiKhuyenMai;
        this.chietKhau = chietKhau;
    }

    // --- Getters & Setters (Đã chính xác) ---
    
    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public LoaiKhuyenMai getLoaiKhuyenMai() {
        return loaiKhuyenMai;
    }

    public void setLoaiKhuyenMai(LoaiKhuyenMai loaiKhuyenMai) {
        this.loaiKhuyenMai = loaiKhuyenMai;
    }

    public double getChietKhau() {
        return chietKhau;
    }

    public void setChietKhau(double chietKhau) {
        this.chietKhau = chietKhau;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhuyenMai);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KhuyenMai other = (KhuyenMai) obj;
        return Objects.equals(maKhuyenMai, other.maKhuyenMai);
    }
}