package entity;

import java.sql.Date;
import java.sql.Time; // THÊM IMPORT NÀY
import java.sql.Timestamp;

public class PhanCongCaLamViec {
    private String maPhanCong;
    private String maNhanVien;
    private String maCa;
    private Date ngayLamViec;
    private String trangThai;
    private Timestamp thoiGianNhanCa;
    private Timestamp thoiGianBanGiao;
    
    // Thông tin join từ các bảng khác
    private String tenNhanVien;
    private String tenCa;
    private Time thoiGianBatDau;  // Sử dụng java.sql.Time
    private Time thoiGianKetThuc; // Sử dụng java.sql.Time

    public PhanCongCaLamViec() {
    }

    public PhanCongCaLamViec(String maPhanCong, String maNhanVien, String maCa, Date ngayLamViec, 
                           String trangThai, Timestamp thoiGianNhanCa, Timestamp thoiGianBanGiao) {
        this.maPhanCong = maPhanCong;
        this.maNhanVien = maNhanVien;
        this.maCa = maCa;
        this.ngayLamViec = ngayLamViec;
        this.trangThai = trangThai;
        this.thoiGianNhanCa = thoiGianNhanCa;
        this.thoiGianBanGiao = thoiGianBanGiao;
    }

    // Getter và Setter
    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) { this.maPhanCong = maPhanCong; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public Date getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(Date ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Timestamp getThoiGianNhanCa() { return thoiGianNhanCa; }
    public void setThoiGianNhanCa(Timestamp thoiGianNhanCa) { this.thoiGianNhanCa = thoiGianNhanCa; }

    public Timestamp getThoiGianBanGiao() { return thoiGianBanGiao; }
    public void setThoiGianBanGiao(Timestamp thoiGianBanGiao) { this.thoiGianBanGiao = thoiGianBanGiao; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public Time getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(Time thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }

    public Time getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(Time thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
}