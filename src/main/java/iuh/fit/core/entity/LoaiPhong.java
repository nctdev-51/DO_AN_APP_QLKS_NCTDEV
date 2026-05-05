package iuh.fit.core.entity;

public enum LoaiPhong {
    PHONG_DON("Phòng đơn"),
    PHONG_DOI("Phòng đôi"),
    PHONG_VIP("Phòng VIP"),
    PHONG_GIA_DINH("Phòng gia đình");

    private final String displayName;

    LoaiPhong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}