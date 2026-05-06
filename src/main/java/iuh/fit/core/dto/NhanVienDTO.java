package iuh.fit.core.dto;

import java.time.LocalDate;

public class NhanVienDTO {
    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private boolean gioiTinh;
    private String cccd;
    private String soDienThoai;
    private boolean trangThai;
    private String loaiNhanVien;
    private LocalDate ngayVaoLam;
    private String queQuan;

    public NhanVienDTO() {
    }

    public NhanVienDTO(String maNhanVien, String hoTen, LocalDate ngaySinh, boolean gioiTinh, String cccd,
                       String soDienThoai, boolean trangThai, String loaiNhanVien, LocalDate ngayVaoLam, String queQuan) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.soDienThoai = soDienThoai;
        this.trangThai = trangThai;
        this.loaiNhanVien = loaiNhanVien;
        this.ngayVaoLam = ngayVaoLam;
        this.queQuan = queQuan;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public String getLoaiNhanVien() { return loaiNhanVien; }
    public void setLoaiNhanVien(String loaiNhanVien) { this.loaiNhanVien = loaiNhanVien; }

    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }

    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }
}