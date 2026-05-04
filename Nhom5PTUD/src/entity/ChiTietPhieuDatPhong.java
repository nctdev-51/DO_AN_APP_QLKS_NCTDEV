package entity;

public class ChiTietPhieuDatPhong {
    private PhieuDatPhong maPhieu;
    private DichVu maDichVu;
    private int soLuong;
    private String ghiChu;

    public ChiTietPhieuDatPhong(PhieuDatPhong phieuDatPhong, DichVu dichVu, int soLuong, String ghiChu) {
        this.maPhieu = phieuDatPhong;
        this.maDichVu = dichVu;
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }

	public PhieuDatPhong getMaPhieu() {
		return maPhieu;
	}

	public void setMaPhieu(PhieuDatPhong maPhieu) {
		this.maPhieu = maPhieu;
	}

	public DichVu getMaDichVu() {
		return maDichVu;
	}

	public void setMaDichVu(DichVu maDichVu) {
		this.maDichVu = maDichVu;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

    
}
