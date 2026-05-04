package entity;

import java.time.LocalTime;
import java.util.Objects;

public class CaLamViec {
    private String maCa;
    private String tenCa;
    private LocalTime thoiGianBatDau;
    private LocalTime thoiGianKetThuc;
    private String ghiChu;
    private boolean trangThai; // True: Đang dùng, False: Không dùng

    public CaLamViec() {
    }

    public CaLamViec(String maCa, String tenCa, LocalTime thoiGianBatDau, LocalTime thoiGianKetThuc, String ghiChu, boolean trangThai) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }
    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public LocalTime getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalTime thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }
    public LocalTime getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(LocalTime thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return tenCa + " (" + thoiGianBatDau + " - " + thoiGianKetThuc + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaLamViec that = (CaLamViec) o;
        return Objects.equals(maCa, that.maCa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCa);
    }
}