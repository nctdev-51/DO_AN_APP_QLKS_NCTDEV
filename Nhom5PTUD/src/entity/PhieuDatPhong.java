package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuDatPhong {
    private String maPhieu;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDate ngayDat;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private double tongTien; // Có thể tính toán hoặc lưu trữ
    private TrangThaiPhieuDat trangThai;

    // Danh sách chi tiết
    private List<Phong> dsPhong;
    private List<ChiTietDichVu> dsDichVu;

    // Constructors
    public PhieuDatPhong() {
        this.dsPhong = new ArrayList<>();
        this.dsDichVu = new ArrayList<>();
    }

    public PhieuDatPhong(String maPhieu) {
        this.maPhieu = maPhieu;
        this.dsPhong = new ArrayList<>();
        this.dsDichVu = new ArrayList<>();
    }

    public PhieuDatPhong(String maPhieu, KhachHang khachHang, NhanVien nhanVien, LocalDate ngayDat, LocalDate ngayNhan,
            LocalDate ngayTra, double tongTien, TrangThaiPhieuDat trangThai) {
        this.maPhieu = maPhieu;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.ngayDat = ngayDat;
        this.ngayNhan = ngayNhan;
        this.ngayTra = ngayTra;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.dsPhong = new ArrayList<>();
        this.dsDichVu = new ArrayList<>();
    }

    // Getters and Setters
    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat;
    }

    public LocalDate getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public TrangThaiPhieuDat getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiPhieuDat trangThai) {
        this.trangThai = trangThai;
    }

    public List<Phong> getDsPhong() {
        return dsPhong;
    }

    public void setDsPhong(List<Phong> dsPhong) {
        this.dsPhong = dsPhong;
    }

    public List<ChiTietDichVu> getDsDichVu() {
        return dsDichVu;
    }

    public void setDsDichVu(List<ChiTietDichVu> dsDichVu) {
        this.dsDichVu = dsDichVu;
    }

    // hashCode and equals
    @Override
    public int hashCode() {
        return Objects.hash(maPhieu);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PhieuDatPhong other = (PhieuDatPhong) obj;
        return Objects.equals(maPhieu, other.maPhieu);
    }
}