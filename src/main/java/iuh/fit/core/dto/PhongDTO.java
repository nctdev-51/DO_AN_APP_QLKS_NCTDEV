package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO: PhongDTO
 * Mô tả: Data Transfer Object cho Phòng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhongDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maPhong;
    private String tenPhong;
    private String maLoaiPhong;
    private String tenLoaiPhong; // Thêm để hiển thị trên UI
    private double giaPhong;
    private String tinhTrang; // Có thể bạn đang dùng field này cũ, tạm giữ lại tránh break code

    // ✅ Thêm mới: Enum kiểm soát trạng thái phòng chặt chẽ
    private TrangThaiPhong trangThaiPhong;

    public enum TrangThaiPhong {
        TRONG,
        DAT,
        DANG_SU_DUNG,
        BAO_TRI
    }
}