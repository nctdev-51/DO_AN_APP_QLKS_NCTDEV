package dto;

import entity.Phong;
import java.util.List;

/**
 * Lớp DTO chứa kết quả lọc phòng từ PhongFilterService.
 */
public class PhongFilterResult {

    /**
     * Enum định nghĩa các loại kết quả có thể có.
     */
    public enum ResultType {
        SINGLE_OPTIONS,  // Tìm thấy các phòng đơn phù hợp
        COMBO_OPTIONS,   // Tìm thấy các tổ hợp phòng phù hợp
        NO_ROOMS_FOUND,  // Không tìm thấy phòng nào
        ERROR            // Có lỗi xảy ra (ví dụ: ngày tháng sai)
    }

    private final ResultType type;
    private final String message;
    private final List<List<Phong>> phongOptions; // List các tổ hợp (tổ hợp 1 phòng, tổ hợp 2+ phòng)

    public PhongFilterResult(ResultType type, String message, List<List<Phong>> phongOptions) {
        this.type = type;
        this.message = message;
        this.phongOptions = phongOptions;
    }

    // Getters
    public ResultType getType() { return type; }
    public String getMessage() { return message; }
    public List<List<Phong>> getPhongOptions() { return phongOptions; }
}