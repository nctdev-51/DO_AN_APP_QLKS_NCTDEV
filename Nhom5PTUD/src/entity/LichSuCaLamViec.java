package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LichSuCaLamViec {
    private String maLichSu;
    private NhanVien nhanVien;
    private CaLamViec caLamViec;
    private LocalDate ngayLamViec; // Cột mới thêm
    private LocalDateTime thoiGianNhanCa;
    private LocalDateTime thoiGianBanGiao;
    private double tienMatDauCa;
    private double tienMatCuoiCa;
    private double tongThuDichVu;
    private double tongThuPhong;
    private double tongChi;

    public LichSuCaLamViec() {
    }

    // Constructor để phân công (chưa có dữ liệu)
    public LichSuCaLamViec(String maLichSu, NhanVien nhanVien, CaLamViec caLamViec, LocalDate ngayLamViec) {
        this.maLichSu = maLichSu;
        this.nhanVien = nhanVien;
        this.caLamViec = caLamViec;
        this.ngayLamViec = ngayLamViec;
        
        // Mặc định là null hoặc 0
        this.thoiGianNhanCa = null;
        this.thoiGianBanGiao = null;
        this.tienMatDauCa = 0;
        this.tienMatCuoiCa = 0;
        this.tongThuDichVu = 0;
        this.tongThuPhong = 0;
        this.tongChi = 0;
    }

    // Constructor đầy đủ
    public LichSuCaLamViec(String maLichSu, NhanVien nhanVien, CaLamViec caLamViec, LocalDate ngayLamViec, LocalDateTime thoiGianNhanCa, LocalDateTime thoiGianBanGiao, double tienMatDauCa, double tienMatCuoiCa, double tongThuDichVu, double tongThuPhong, double tongChi) {
        this.maLichSu = maLichSu;
        this.nhanVien = nhanVien;
        this.caLamViec = caLamViec;
        this.ngayLamViec = ngayLamViec;
        this.thoiGianNhanCa = thoiGianNhanCa;
        this.thoiGianBanGiao = thoiGianBanGiao;
        this.tienMatDauCa = tienMatDauCa;
        this.tienMatCuoiCa = tienMatCuoiCa;
        this.tongThuDichVu = tongThuDichVu;
        this.tongThuPhong = tongThuPhong;
        this.tongChi = tongChi;
    }

    // Getters and Setters
    public String getMaLichSu() { return maLichSu; }
    public void setMaLichSu(String maLichSu) { this.maLichSu = maLichSu; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public CaLamViec getCaLamViec() { return caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) { this.caLamViec = caLamViec; }
    public LocalDate getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(LocalDate ngayLamViec) { this.ngayLamViec = ngayLamViec; }
    public LocalDateTime getThoiGianNhanCa() { return thoiGianNhanCa; }
    public void setThoiGianNhanCa(LocalDateTime thoiGianNhanCa) { this.thoiGianNhanCa = thoiGianNhanCa; }
    public LocalDateTime getThoiGianBanGiao() { return thoiGianBanGiao; }
    public void setThoiGianBanGiao(LocalDateTime thoiGianBanGiao) { this.thoiGianBanGiao = thoiGianBanGiao; }
    public double getTienMatDauCa() { return tienMatDauCa; }
    public void setTienMatDauCa(double tienMatDauCa) { this.tienMatDauCa = tienMatDauCa; }
    public double getTienMatCuoiCa() { return tienMatCuoiCa; }
    public void setTienMatCuoiCa(double tienMatCuoiCa) { this.tienMatCuoiCa = tienMatCuoiCa; }
    public double getTongThuDichVu() { return tongThuDichVu; }
    public void setTongThuDichVu(double tongThuDichVu) { this.tongThuDichVu = tongThuDichVu; }
    public double getTongThuPhong() { return tongThuPhong; }
    public void setTongThuPhong(double tongThuPhong) { this.tongThuPhong = tongThuPhong; }
    public double getTongChi() { return tongChi; }
    public void setTongChi(double tongChi) { this.tongChi = tongChi; }

    // Trạng thái của ca
    public String getTrangThai() {
        if (thoiGianNhanCa == null) {
            return "Chưa nhận ca";
        }
        if (thoiGianBanGiao == null) {
            return "Đang làm việc";
        }
        return "Đã bàn giao";
    }
}