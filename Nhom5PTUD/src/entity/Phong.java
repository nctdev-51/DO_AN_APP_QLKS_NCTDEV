package entity;

import java.util.Objects;

public class Phong {
    private String maPhong;
    private String tenPhong;
    private LoaiPhong loaiPhong;
    private int sucChua;
    private String loaiGiuong;
    private double giaPhong;
    private TinhTrangPhong tinhTrang;

    // Constructors
    public Phong() {
    }

    public Phong(String maPhong) {
        this.maPhong = maPhong;
    }

    public Phong(String maPhong, String tenPhong, LoaiPhong loaiPhong, int sucChua, String loaiGiuong, double giaPhong, TinhTrangPhong tinhTrang) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.loaiPhong = loaiPhong;
        this.sucChua = sucChua;
        this.loaiGiuong = loaiGiuong;
        this.giaPhong = giaPhong;
        this.tinhTrang = tinhTrang;
    }

    // Getters and Setters
    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public String getLoaiGiuong() {
        return loaiGiuong;
    }

    public void setLoaiGiuong(String loaiGiuong) {
        this.loaiGiuong = loaiGiuong;
    }

    public double getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(double giaPhong) {
        this.giaPhong = giaPhong;
    }

    public TinhTrangPhong getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(TinhTrangPhong tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    // hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(maPhong);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Phong other = (Phong) obj;
        return Objects.equals(maPhong, other.maPhong);
    }
}