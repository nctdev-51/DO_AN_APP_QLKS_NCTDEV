package iuh.fit.core.entity;

public enum LoaiNhanVien {
    NHAN_VIEN_LE_TAN("Lễ tân"),
    NHAN_VIEN_PHONG("Nhân viên phòng"),
    NHAN_VIEN_SAN_KHAU("Nhân viên sân khấu"),

    // ĐÃ SỬA: Đổi từ QUAN_LY thành NHAN_VIEN_QUAN_LY cho khớp với Database
    NHAN_VIEN_QUAN_LY("Quản lý"),

    GIAM_DOC("Giám đốc");

    private final String displayName;

    LoaiNhanVien(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}