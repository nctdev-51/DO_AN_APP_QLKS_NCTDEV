package iuh.fit.core.entity;

/**
 * Enum định nghĩa các loại khách hàng
 * 
 * Tầng: CORE - Domain Entity
 * Trách nhiệm: Định nghĩa các hằng số kinh doanh
 */
public enum LoaiKhachHang {
    KHACH_HOI_VIEN("Khách hội viên"),
    KHACH_THUONG_XUYÊN("Khách thường xuyên"),
    KHACH_MOI("Khách mới");

    private final String displayName;

    LoaiKhachHang(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

