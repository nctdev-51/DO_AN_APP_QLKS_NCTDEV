package entity;

public enum TinhTrangPhong {
    TRONG("Trống"),
    DANG_SU_DUNG("Đang sử dụng"),
    DANG_DON_DEP("Đang dọn dẹp"),
    DANG_SUA_CHUA("Đang sửa chữa"),
    DA_DAT("Đã đặt");

    private String trangThai;

    TinhTrangPhong(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return this.trangThai;
    }

    // Chuyển đổi từ chuỗi trong DB (VD: N'Đã đặt') sang Enum
    public static TinhTrangPhong fromString(String text) {
        if (text != null) {
            for (TinhTrangPhong e : TinhTrangPhong.values()) {
                // So sánh cả name() (DA_DAT) và toString() (Đã đặt)
                if (text.equalsIgnoreCase(e.name()) || text.equalsIgnoreCase(e.trangThai)) {
                    return e;
                }
            }
        }
        return TinhTrangPhong.TRONG; // Mặc định
    }
}