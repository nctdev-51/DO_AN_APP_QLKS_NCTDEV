package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuCaLamViec")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuCaLamViec {
    @Id
    @Column(name = "maLichSu", length = 30)
    private String maLichSu;

    @Column(name = "maNhanVien", length = 5, insertable = false, updatable = false)
    private String maNhanVien;

    @Column(name = "maCa", length = 20, insertable = false, updatable = false)
    private String maCa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maCa")
    private CaLamViec caLamViec;

    @Column(name = "thoiGianNhanCa", nullable = false)
    private LocalDateTime thoiGianNhanCa;

    @Column(name = "thoiGianGiaoCa")
    private LocalDateTime thoiGianGiaoCa;

    @Column(name = "tienDauCa")
    private double tienDauCa;

    @Column(name = "tongThuTrongCa")
    private double tongThuTrongCa;

    @Column(name = "tienCuoiCaThucTe")
    private double tienCuoiCaThucTe;

    @Column(name = "tienChenhLech")
    private double tienChenhLech;

    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    @Column(name = "trangThai", length = 50)
    private String trangThai; // DANG_LAM, DA_GIAO_CA
}