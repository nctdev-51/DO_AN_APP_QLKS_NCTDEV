package entity;

public enum LoaiKhachHang {
	KHACH_VANG_LAI("Khách vãng lai"),
	 KHACH_HOI_VIEN("Khách hội viên");

	    private final String moTa;

	    LoaiKhachHang(String moTa) {
	        this.moTa = moTa;
	    }

	    public String getMoTa() {
	        return moTa;
	    }
}
