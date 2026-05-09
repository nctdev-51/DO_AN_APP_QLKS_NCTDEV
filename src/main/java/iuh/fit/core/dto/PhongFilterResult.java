package iuh.fit.core.dto;

import iuh.fit.core.entity.Phong;

import java.io.Serializable;
import java.util.List;

/**
 * DTO: PhongFilterResult
 * Mô tả: Kết quả tìm kiếm phòng với các lựa chọn
 */
public class PhongFilterResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ResultType {
        SINGLE_OPTIONS,    // Có phòng đơn
        COMBO_OPTIONS,     // Có tổ hợp phòng
        NO_ROOMS_FOUND,    // Không tìm được phòng
        ERROR              // Lỗi
    }

    private ResultType resultType;
    private String message;
    private List<List<Phong>> options;  // Danh sách các lựa chọn (mỗi lựa chọn là 1 hoặc nhiều phòng)

    public PhongFilterResult() {
    }

    public PhongFilterResult(ResultType resultType, String message, List<List<Phong>> options) {
        this.resultType = resultType;
        this.message = message;
        this.options = options;
    }

    // Getters & Setters
    public ResultType getResultType() { return resultType; }
    public void setResultType(ResultType resultType) { this.resultType = resultType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<List<Phong>> getOptions() { return options; }
    public void setOptions(List<List<Phong>> options) { this.options = options; }

    public boolean isSuccess() {
        return resultType != ResultType.ERROR && resultType != ResultType.NO_ROOMS_FOUND;
    }
}

