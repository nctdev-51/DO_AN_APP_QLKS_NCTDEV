package entity;

public class ChiTietDatPhong {
    private PhieuDatPhong phieuDatPhong;
    private Phong phong;

    public ChiTietDatPhong(PhieuDatPhong phieuDatPhong, Phong phong) {
        this.phieuDatPhong = phieuDatPhong;
        this.phong = phong;
    }
    
    // Getters and Setters
    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }
    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }
}