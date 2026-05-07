package entity;

public class ChiTietHoaDon {
    private HoaDon maHoaDon;
    private DichVu maDichVu;
    private int soLuong;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon, DichVu dichVu, int soLuong) {
        this.maHoaDon = hoaDon;
        this.maDichVu = dichVu;
        this.soLuong = soLuong;
    }

    public HoaDon getHoaDon() {
        return maHoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.maHoaDon = hoaDon;
    }

    public DichVu getDichVu() {
        return maDichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.maDichVu = dichVu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon {" +
                "maHoaDon=" + (maHoaDon != null ? maHoaDon.getMaHoaDon() : "null") +
                ", maDichVu=" + (maDichVu != null ? maDichVu.getMaDichVu() : "null") +
                ", soLuong=" + soLuong +
                '}';
    }
}
