package entity;

public enum LoaiPhong {
    DON("Đơn"),
    DOI("Đôi"),
    GIADINH("Gia đình"),
    VIP("VIP");

    private String tenLoaiPhong;

    LoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    @Override
    public String toString() {
        return this.tenLoaiPhong;
    }

    // Chuyển đổi từ chuỗi trong DB (VD: 'DON') sang Enum
    public static LoaiPhong fromString(String text) {
        if (text != null) {
            for (LoaiPhong e : LoaiPhong.values()) {
                if (text.equalsIgnoreCase(e.name())) {
                    return e;
                }
            }
        }
        return null; // Hoặc ném ngoại lệ
    }
}