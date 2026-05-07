package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DichVu") // 👉 Sửa thành DichVu
public class DichVu {

    @Id
    @Column(name = "maDichVu", length = 20) // 👉 Sửa thành maDichVu
    private String maDichVu;

    @Column(name = "tenDichVu", length = 100, nullable = false) // 👉 Sửa thành tenDichVu
    private String tenDichVu;

    @Column(name = "giaTien") // 👉 Sửa thành giaTien
    private double giaTien;

    @Column(name = "moTa", length = 255) // 👉 Sửa thành moTa
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