package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDTO {
    private String maBaoCao;
    private String maNhanVien;
    private String hoTenNhanVien; // Lấy từ Entity NhanVien
    private String tieuDe;
    private String noiDung;
    private String phanLoai;
    private LocalDateTime ngayTao;
    private String trangThai;
}