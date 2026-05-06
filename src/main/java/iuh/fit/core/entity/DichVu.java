package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dich_vu")
public class DichVu {

    @Id
    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;

    @Column(name = "ten_dich_vu", length = 100, nullable = false)
    private String tenDichVu;

    @Column(name = "gia_tien")
    private double giaTien;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    public DichVu() {
    }

    public DichVu(String maDichVu, String tenDichVu, double giaTien, String moTa) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.giaTien = giaTien;
        this.moTa = moTa;
    }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public double getGiaTien() { return giaTien; }
    public void setGiaTien(double giaTien) { this.giaTien = giaTien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}