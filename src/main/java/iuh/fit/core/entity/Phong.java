package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Phong")
public class Phong {

    @Id
    @Column(name = "maPhong", length = 4)
    private String maPhong;

    @Column(name = "tenPhong", length = 100)
    private String tenPhong;

    @Column(name = "giaPhong")
    private Double giaPhong;

    @Column(name = "maLoaiPhong", length = 20, nullable = false)
    private String maLoaiPhong;

    @Column(name = "tinhTrang", length = 50)
    private String tinhTrang;

    public Phong() {
    }

    public Phong(String maPhong, String tenPhong, Double giaPhong, String maLoaiPhong, String tinhTrang) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.giaPhong = giaPhong;
        this.maLoaiPhong = maLoaiPhong;
        this.tinhTrang = tinhTrang;
    }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public Double getGiaPhong() { return giaPhong; }
    public void setGiaPhong(Double giaPhong) { this.giaPhong = giaPhong; }

    public String getMaLoaiPhong() { return maLoaiPhong; }
    public void setMaLoaiPhong(String maLoaiPhong) { this.maLoaiPhong = maLoaiPhong; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
}