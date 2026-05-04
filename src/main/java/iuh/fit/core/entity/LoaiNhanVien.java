package iuh.fit.core.entity;

/**
 * Enum định nghĩa các loại nhân viên trong khách sạn
 * 
 * Tầng: CORE - Domain Entity
 * Trách nhiệm: Định nghĩa các hằng số kinh doanh
 */
public enum LoaiNhanVien {
    NHAN_VIEN_LE_TAN("Lễ tân"),
    NHAN_VIEN_PHONG("Nhân viên phòng"),
    NHAN_VIEN_SAN_KHAU("Nhân viên sân khấu"),
    QUAN_LY("Quản lý"),
    GIAM_DOC("Giám đốc");

    private final String displayName;

    LoaiNhanVien(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

