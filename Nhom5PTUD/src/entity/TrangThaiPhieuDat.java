package entity;

public enum TrangThaiPhieuDat {
    CHO_NHAN_PHONG("Chờ nhận phòng"),
    DA_THANH_TOAN_TRUOC("Đã thanh toán (chờ nhận)"), // <<< THÊM DÒNG NÀY
    DA_NHAN("Đã nhận"),
    DA_THANH_TOAN("Đã thanh toán"), // (Trạng thái này giờ có nghĩa là Đã trả phòng)
    DA_HUY("Đã hủy");

    private final String displayName;

    TrangThaiPhieuDat(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static TrangThaiPhieuDat fromString(String text) {
        if (text != null) {
            for (TrangThaiPhieuDat b : TrangThaiPhieuDat.values()) {
                // So sánh cả tên Enum (DA_HUY) và tên hiển thị (Đã hủy)
                if (text.equalsIgnoreCase(b.displayName) || text.equalsIgnoreCase(b.name())) {
                    return b;
                }
            }
        }
        // Thêm các trạng thái cũ của bạn vào đây nếu cần
        if ("Đã xác nhận".equalsIgnoreCase(text)) return CHO_NHAN_PHONG;
        if ("Hủy".equalsIgnoreCase(text)) return DA_HUY;
        
        return CHO_NHAN_PHONG; // Mặc định
    }
}