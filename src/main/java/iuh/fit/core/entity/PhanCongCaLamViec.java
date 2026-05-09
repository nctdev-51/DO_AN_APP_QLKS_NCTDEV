package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Entity
@Table(name = "phan_cong_ca_lam_viec")
public class PhanCongCaLamViec {

    @Id
    @Column(name = "ma_phan_cong", length = 20)
    private String maPhanCong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_ca")
    private CaLamViec caLamViec;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "CHUA_LAM";

    @Column(name = "ghi_chu")
    private String ghiChu;
}