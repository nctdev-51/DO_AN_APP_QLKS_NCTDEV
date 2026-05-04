package entity;

public class TaiKhoan {
	private NhanVien nhanVien;
	private String taiKhoan;
	private String matKhau;
	
	public TaiKhoan() {
		
	}

	public TaiKhoan(NhanVien nhanVien, String taiKhoan, String matKhau) {
		this.nhanVien = nhanVien;
		this.taiKhoan = taiKhoan;
		this.matKhau = matKhau;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public String getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(String taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	@Override
	public String toString() {
		return "TaiKhoan [nhanVien=" + nhanVien + ", taiKhoan=" + taiKhoan + ", matKhau=" + matKhau + "]";
	}
	
	
	
	
}