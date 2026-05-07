package entity;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private LocalDate ngaySinh;
    private boolean gioiTinh;
    private String CCCD;
    private String soDienThoai;
    private boolean trangThai;
    private LoaiNhanVien loaiNhanVien;
    private LocalDate ngayVaoLam;
    private String queQuan;

    public NhanVien() {
        this("", "", LocalDate.now(), true, "", "", true, LoaiNhanVien.NHAN_VIEN_LE_TAN, LocalDate.now(), "");
    }

    public NhanVien(String maNhanVien, String hoTen, LocalDate ngaySinh, boolean gioiTinh, String CCCD,
                   String soDienThoai, boolean trangThai, LoaiNhanVien loaiNhanVien, LocalDate ngayVaoLam, String queQuan) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.CCCD = CCCD;
        this.soDienThoai = soDienThoai;
        this.trangThai = trangThai;
        this.loaiNhanVien = loaiNhanVien;
        this.ngayVaoLam = ngayVaoLam;
        this.queQuan = queQuan;
    }

    // Getter và Setter
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getCCCD() { return CCCD; }
    public void setCCCD(String CCCD) { this.CCCD = CCCD; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
    public LoaiNhanVien getLoaiNhanVien() { return loaiNhanVien; }
    public void setLoaiNhanVien(LoaiNhanVien loaiNhanVien) { this.loaiNhanVien = loaiNhanVien; }
    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    public String getQueQuan() { return queQuan; }
    public void setQueQuan(String queQuan) { this.queQuan = queQuan; }

    /**
     * Hiển thị trong JComboBox
     */
    @Override
    public String toString() {
        return hoTen + " (" + maNhanVien + ")";
    }
}