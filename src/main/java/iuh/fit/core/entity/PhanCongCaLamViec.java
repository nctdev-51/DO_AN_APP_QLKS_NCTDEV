package iuh.fit.core.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "phan_cong_ca_lam_viec")
public class PhanCongCaLamViec {

    @Id
    @Column(name = "ma_phan_cong", length = 20)
    private String maPhanCong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_ca")
    private CaLamViec caLamViec;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;

    public PhanCongCaLamViec() {
    }

    public PhanCongCaLamViec(String maPhanCong, NhanVien nhanVien, CaLamViec caLamViec, LocalDate ngayLamViec) {
        this.maPhanCong = maPhanCong;
        this.nhanVien = nhanVien;
        this.caLamViec = caLamViec;
        this.ngayLamViec = ngayLamViec;
    }

    public String getMaPhanCong() { return maPhanCong; }
    public void setMaPhanCong(String maPhanCong) { this.maPhanCong = maPhanCong; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    public CaLamViec getCaLamViec() { return caLamViec; }
    public void setCaLamViec(CaLamViec caLamViec) { this.caLamViec = caLamViec; }

    public LocalDate getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(LocalDate ngayLamViec) { this.ngayLamViec = ngayLamViec; }
}