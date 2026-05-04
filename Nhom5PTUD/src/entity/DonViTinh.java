package entity;

public enum DonViTinh {
    LON("Lon"),
    CHAI("Chai"),
    PHAN("Phần"),
    DIA("Đĩa"),
    TO("Tô"),
    LY("Ly"),
    LAN("Lần"), // Ví dụ: Giặt ủi 1 lần
    GIO("Giờ"), // Ví dụ: Karaoke 1 giờ
    CAI("Cái"); // Mặc định

    private final String displayName;

    DonViTinh(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public static DonViTinh fromString(String text) {
        if (text != null) {
            for (DonViTinh b : DonViTinh.values()) {
                if (text.equalsIgnoreCase(b.displayName)) {
                    return b;
                }
            }
        }
        return CAI; // Trả về giá trị mặc định
    }
}