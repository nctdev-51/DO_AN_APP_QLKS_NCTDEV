package iuh.fit.core.entity;

/**
 * Enum định nghĩa các trạng thái của phòng
 * 
 * Tầng: CORE - Domain Entity
 * Trách nhiệm: Định nghĩa các hằng số kinh doanh
 */
public enum TinhTrangPhong {
    PHONG_TRONG("Phòng trống"),
    PHONG_HAN_CHE("Phòng hạn chế"),
    DANG_SUA_CHUA("Đang sửa chữa"),
    PHONG_NGOAI_SU_DUNG("Phòng ngoài sử dụng");

    private final String displayName;

    TinhTrangPhong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

