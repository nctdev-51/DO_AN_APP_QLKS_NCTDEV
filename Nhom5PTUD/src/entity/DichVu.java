package entity;

// (Import java.util.Objects nếu chưa có)

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private double giaTien;
    private String moTa;
    
    // --- THAY ĐỔI: Chuyển từ String sang Enum ---
    private LoaiDichVu loaiDichVu; 
    private DonViTinh donViTinh; 
    private String hinhAnh; 

    public DichVu() {
    }

    // Constructor cũ (giữ lại để tương thích)
    public DichVu(String maDichVu, String tenDichVu, double giaTien, String moTa) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.giaTien = giaTien;
        this.moTa = moTa;
        // Gán giá trị mặc định cho Enum
        this.loaiDichVu = LoaiDichVu.KHAC;
        this.donViTinh = DonViTinh.CAI;
        this.hinhAnh = "data/services/default.png";
    }
    
    // --- THAY ĐỔI: Constructor đầy đủ ---
    public DichVu(String maDichVu, String tenDichVu, double giaTien, String moTa, 
                  LoaiDichVu loaiDichVu, DonViTinh donViTinh, String hinhAnh) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.giaTien = giaTien;
        this.moTa = moTa;
        this.loaiDichVu = loaiDichVu;
        this.donViTinh = donViTinh;
        this.hinhAnh = hinhAnh;
    }

    // --- Getter và Setter (Thay đổi kiểu dữ liệu) ---
    
    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public double getGiaTien() { return giaTien; }
    public void setGiaTien(double giaTien) { this.giaTien = giaTien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    // THAY ĐỔI
    public LoaiDichVu getLoaiDichVu() { return loaiDichVu; }
    public void setLoaiDichVu(LoaiDichVu loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    // THAY ĐỔI
    public DonViTinh getDonViTinh() { return donViTinh; }
    public void setDonViTinh(DonViTinh donViTinh) { this.donViTinh = donViTinh; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    
    
    @Override
	public String toString() {
		return "DichVu [maDichVu=" + maDichVu + ", tenDichVu=" + tenDichVu + ", giaTien=" + giaTien + ", moTa=" + moTa
				+ ", loaiDichVu=" + loaiDichVu + ", donViTinh=" + donViTinh + ", hinhAnh=" + hinhAnh + "]";
	}

	@Override
    public int hashCode() {
        return java.util.Objects.hash(maDichVu);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DichVu dichVu = (DichVu) obj;
        return java.util.Objects.equals(maDichVu, dichVu.maDichVu);
    }
}