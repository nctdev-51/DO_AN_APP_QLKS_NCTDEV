package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "BaoCao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCao {
    @Id
    @Column(name = "maBaoCao", length = 30)
    private String maBaoCao;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @Column(name = "tieuDe", length = 100, nullable = false)
    private String tieuDe;

    @Column(name = "noiDung", length = 1000, nullable = false)
    private String noiDung;

    @Column(name = "phanLoai", length = 50)
    private String phanLoai; // SU_CO, TAI_CHINH, DE_XUAT, KHAC

    @Column(name = "ngayTao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "CHUA_XEM"; // CHUA_XEM, DA_XEM, DA_XU_LY

    // Thêm vào các trường sau
    @Column(name = "hinhAnh", length = 255)
    private String hinhAnh; // Lưu đường dẫn file ảnh

    @Column(name = "phanHoiQuanLy", length = 500)
    private String phanHoiQuanLy;
}