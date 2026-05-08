package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon {

    @EmbeddedId
    private ChiTietHoaDonId id;

    @Column(name = "soLuong")
    private int soLuong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maHoaDon", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;

    public String getMaHoaDon() { return id != null ? id.maHoaDon : null; }
    public void setMaHoaDon(String maHoaDon) {
        if (id == null) id = new ChiTietHoaDonId();
        id.maHoaDon = maHoaDon;
    }

    public String getMaDichVu() { return id != null ? id.maDichVu : null; }
    public void setMaDichVu(String maDichVu) {
        if (id == null) id = new ChiTietHoaDonId();
        id.maDichVu = maDichVu;
    }
}