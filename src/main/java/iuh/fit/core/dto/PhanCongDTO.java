package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCongDTO {
    private String maPhanCong;
    private String maNhanVien;
    private String tenNhanVien;
    private String maCa;
    private String tenCa;
    private LocalDate ngayLamViec;
    private String trangThai; // CHUA_LAM, VANG_MAT, NGHI_PHEP
    private String ghiChu;
}