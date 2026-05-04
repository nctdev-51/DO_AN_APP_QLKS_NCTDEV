package entity;

public enum LoaiDichVu {
    THUC_AN("Thức ăn"),
    DO_UONG("Đồ uống"),
    DICH_VU("Dịch vụ"), // Ví dụ: Giặt ủi, Spa
    KHAC("Khác");

    private final String displayName;

    LoaiDichVu(String displayName) {
        this.displayName = displayName;
    }

    // Dùng để hiển thị ra JComboBox hoặc JTable
    @Override
    public String toString() {
        return this.displayName;
    }

    // Dùng để đọc từ CSDL (nếu CSDL lưu chuỗi "Thức ăn")
    public static LoaiDichVu fromString(String text) {
        if (text != null) {
            for (LoaiDichVu b : LoaiDichVu.values()) {
                if (text.equalsIgnoreCase(b.displayName)) {
                    return b;
                }
            }
        }
        return KHAC; // Trả về giá trị mặc định nếu không tìm thấy
    }
}