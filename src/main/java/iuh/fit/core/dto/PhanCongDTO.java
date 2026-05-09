package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCongDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maPhanCong;
    private String maNhanVien;
    private String tenNhanVien;
    private String maCa;
    private String tenCa;
    private LocalDate ngayLamViec;
    private String trangThai; // CHUA_LAM, VANG_MAT, NGHI_PHEP
    private String ghiChu;
}