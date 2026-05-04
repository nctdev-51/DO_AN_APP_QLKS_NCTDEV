package dto;

import java.sql.Timestamp;

public class PhanCongDTO {
    private String maLichSu;
    private String tenNhanVien;
    private String tenCa;
    private Timestamp thoiGianNhanCa;
    private Timestamp thoiGianBanGiao;
    private boolean daNhanCa; // (tienMatDauCa != null)

    public PhanCongDTO(String maLichSu, String tenNhanVien, String tenCa, Timestamp thoiGianNhanCa, Timestamp thoiGianBanGiao, boolean daNhanCa) {
        this.maLichSu = maLichSu;
        this.tenNhanVien = tenNhanVien;
        this.tenCa = tenCa;
        this.thoiGianNhanCa = thoiGianNhanCa;
        this.thoiGianBanGiao = thoiGianBanGiao;
        this.daNhanCa = daNhanCa;
    }

    // Getters
    public String getMaLichSu() { return maLichSu; }
    public String getTenNhanVien() { return tenNhanVien; }
    public String getTenCa() { return tenCa; }
    public Timestamp getThoiGianNhanCa() { return thoiGianNhanCa; }
    public Timestamp getThoiGianBanGiao() { return thoiGianBanGiao; }
    public boolean isDaNhanCa() { return daNhanCa; }

    // Dùng cho JTable
    public String getTrangThai() {
        if (thoiGianBanGiao != null) {
            return "Đã hoàn thành";
        } else if (daNhanCa) {
            return "Đang làm";
        } else {
            return "Chờ nhận ca";
        }
    }
}