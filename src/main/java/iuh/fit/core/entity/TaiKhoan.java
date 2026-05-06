package iuh.fit.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @Column(name = "tenDangNhap", length = 50)
    private String tenDangNhap;

    @Column(name = "matKhau", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "trangThaiTK", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean trangThaiTK;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien", referencedColumnName = "maNhanVien")
    private NhanVien nhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(String tenDangNhap, String matKhau, boolean trangThaiTK, NhanVien nhanVien) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.trangThaiTK = trangThaiTK;
        this.nhanVien = nhanVien;
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public boolean isTrangThaiTK() { return trangThaiTK; }
    public void setTrangThaiTK(boolean trangThaiTK) { this.trangThaiTK = trangThaiTK; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
}