package iuh.fit.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietHoaDonId implements Serializable {

    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    @Column(name = "ma_dich_vu", length = 20)
    private String maDichVu;

    public ChiTietHoaDonId() {
    }

    public ChiTietHoaDonId(String maHoaDon, String maDichVu) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDonId that = (ChiTietHoaDonId) o;
        return Objects.equals(maHoaDon, that.maHoaDon) && Objects.equals(maDichVu, that.maDichVu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon, maDichVu);
    }
}

