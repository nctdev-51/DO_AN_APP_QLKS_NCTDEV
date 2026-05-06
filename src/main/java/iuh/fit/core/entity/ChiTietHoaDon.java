package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chi_tiet_hoa_don")
public class ChiTietHoaDon {

    @EmbeddedId
    private ChiTietHoaDonId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_hoa_don", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_dich_vu", insertable = false, updatable = false)
    private DichVu dichVu;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "gia_tien_tung_dich_vu")
    private double giaTienTungDichVu;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(ChiTietHoaDonId id, HoaDon hoaDon, DichVu dichVu, int soLuong, double giaTienTungDichVu) {
        this.id = id;
        this.hoaDon = hoaDon;
        this.dichVu = dichVu;
        this.soLuong = soLuong;
        this.giaTienTungDichVu = giaTienTungDichVu;
    }

    public ChiTietHoaDonId getId() { return id; }
    public void setId(ChiTietHoaDonId id) { this.id = id; }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }

    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getGiaTienTungDichVu() { return giaTienTungDichVu; }
    public void setGiaTienTungDichVu(double giaTienTungDichVu) { this.giaTienTungDichVu = giaTienTungDichVu; }

    public double getThanhTien() {
        return soLuong * giaTienTungDichVu;
    }
}

