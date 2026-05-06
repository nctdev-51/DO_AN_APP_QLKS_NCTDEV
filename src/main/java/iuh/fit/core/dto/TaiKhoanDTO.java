package iuh.fit.core.dto;

public class TaiKhoanDTO {
    private String taiKhoan;
    private String matKhau;
    private String maNhanVien;
    private String hoTenNhanVien;

    public TaiKhoanDTO() {
    }

    public TaiKhoanDTO(String taiKhoan, String matKhau, String maNhanVien, String hoTenNhanVien) {
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.maNhanVien = maNhanVien;
        this.hoTenNhanVien = hoTenNhanVien;
    }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTenNhanVien() { return hoTenNhanVien; }
    public void setHoTenNhanVien(String hoTenNhanVien) { this.hoTenNhanVien = hoTenNhanVien; }
}