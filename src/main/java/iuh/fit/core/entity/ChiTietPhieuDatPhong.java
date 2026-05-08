package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ChiTietPhieuDatPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuDatPhong {

    @EmbeddedId
    private ChiTietPhieuDatPhongId id;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhieu", insertable = false, updatable = false)
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;

    public String getMaPhieu() { return id != null ? id.maPhieu : null; }
    public void setMaPhieu(String maPhieu) {
        if (id == null) id = new ChiTietPhieuDatPhongId();
        id.maPhieu = maPhieu;
    }

    public String getMaDichVu() { return id != null ? id.maDichVu : null; }
    public void setMaDichVu(String maDichVu) {
        if (id == null) id = new ChiTietPhieuDatPhongId();
        id.maDichVu = maDichVu;
    }
}