package entity;

import java.time.LocalDateTime; // <<< THÊM IMPORT

public class ChiTietDichVu {
    private PhieuDatPhong phieuDatPhong; 
    private Phong phong; 
    private DichVu dichVu;
    private LocalDateTime thoiGianGoi; // <<< THÊM THUỘC TÍNH MỚI
    private int soLuong;
    private String ghiChu;

    // Constructors
    public ChiTietDichVu() {
    }

    /**
     * Constructor đầy đủ (dùng khi TẢI từ CSDL)
     */
    public ChiTietDichVu(PhieuDatPhong phieuDatPhong, Phong phong, DichVu dichVu, LocalDateTime thoiGianGoi, int soLuong, String ghiChu) {
        this.phieuDatPhong = phieuDatPhong;
        this.phong = phong;
        this.dichVu = dichVu;
        this.thoiGianGoi = thoiGianGoi; // <<< THÊM VÀO CONSTRUCTOR
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }
    
    /**
     * Constructor tiện lợi (dùng khi TẠO MỚI một dịch vụ)
     * Tự động lấy thời gian hiện tại, giống như DEFAULT GETDATE()
     */
    public ChiTietDichVu(PhieuDatPhong phieuDatPhong, Phong phong, DichVu dichVu, int soLuong, String ghiChu) {
        this.phieuDatPhong = phieuDatPhong;
        this.phong = phong;
        this.dichVu = dichVu;
        this.thoiGianGoi = LocalDateTime.now(); // <<< TỰ ĐỘNG GÁN THỜI GIAN HIỆN TẠI
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }


    // Getters and Setters
    public PhieuDatPhong getPhieuDatPhong() {
        return phieuDatPhong;
    }

    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        this.phieuDatPhong = phieuDatPhong;
    }

    public Phong getPhong() {
        return phong;
    }

    public void setPhong(Phong phong) {
        this.phong = phong;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
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

	public LocalDateTime getThoiGianGoi() {
		return thoiGianGoi;
	}

	public void setThoiGianGoi(LocalDateTime thoiGianGoi) {
		this.thoiGianGoi = thoiGianGoi;
	}
}