package iuh.fit.core.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauPheDuyetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maYeuCau;
    private String maNhanVien;
    private String tenNhanVien;
    private LocalDateTime thoiGianYeuCau;
    private double tienDauCa;
    private String lyDo;
    private String trangThai;
    private String maQuanLy;
    private LocalDateTime thoiGianDuyet;
}