package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maBaoCao;
    private String maNhanVien;
    private String hoTenNhanVien;
    private String tieuDe;
    private String noiDung;
    private String phanLoai;
    private LocalDateTime ngayTao;
    private String trangThai;
    private String hinhAnh;
    private String phanHoiQuanLy;
}