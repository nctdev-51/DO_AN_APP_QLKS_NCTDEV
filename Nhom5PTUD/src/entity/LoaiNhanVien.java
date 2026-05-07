package entity;

public enum LoaiNhanVien {
    NHAN_VIEN_LE_TAN("Nhân viên lễ tân"),
    NHAN_VIEN_QUAN_LY("Nhân viên quản lý");

    private final String tenLoai;

    LoaiNhanVien(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    @Override
    public String toString() {
        return this.tenLoai;
    }

    // Dùng để chuyển đổi từ String trong DB sang Enum
    public static LoaiNhanVien fromString(String text) {
        if (text != null) {
            for (LoaiNhanVien b : LoaiNhanVien.values()) {
                if (text.equalsIgnoreCase(b.name()) || text.equalsIgnoreCase(b.tenLoai)) {
                    return b;
                }
            }
        }
        return LoaiNhanVien.NHAN_VIEN_LE_TAN; // Mặc định
    }
}