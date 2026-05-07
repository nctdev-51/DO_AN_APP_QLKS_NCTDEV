package entity;

public enum LoaiKhuyenMai {
	THEO_KHACH_HANG("Theo khách hàng"),
	THEO_PHONG("Theo phòng");

	    private final String moTa;

	    LoaiKhuyenMai(String moTa) {
	        this.moTa = moTa;
	    }

	    public String getMoTa() {
	        return moTa;
	    }
}
