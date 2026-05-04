package entity;

import java.util.Objects;

public class HinhThucThanhToan {
    private String maHinhThuc;
    private String tenHinhThuc;
    private double phiGiaoDich;

    public HinhThucThanhToan() {
    }

    public HinhThucThanhToan(String maHinhThuc) {
        this.maHinhThuc = maHinhThuc;
    }

    public HinhThucThanhToan(String maHinhThuc, String tenHinhThuc, double phiGiaoDich) {
        this.maHinhThuc = maHinhThuc;
        this.tenHinhThuc = tenHinhThuc;
        this.phiGiaoDich = phiGiaoDich;
    }

    public String getMaHinhThuc() {
        return maHinhThuc;
    }

    public void setMaHinhThuc(String maHinhThuc) {
        this.maHinhThuc = maHinhThuc;
    }

    public String getTenHinhThuc() {
        return tenHinhThuc;
    }

    public void setTenHinhThuc(String tenHinhThuc) {
        this.tenHinhThuc = tenHinhThuc;
    }

    public double getPhiGiaoDich() {
        return phiGiaoDich;
    }

    public void setPhiGiaoDich(double phiGiaoDich) {
        this.phiGiaoDich = phiGiaoDich;
    }

    @Override
    public String toString() {
        return tenHinhThuc; // Hiển thị tên ra JComboBox
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHinhThuc);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HinhThucThanhToan other = (HinhThucThanhToan) obj;
        return Objects.equals(maHinhThuc, other.maHinhThuc);
    }
}