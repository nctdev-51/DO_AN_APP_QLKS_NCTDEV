package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "phan_cong_ca_lam_viec")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCongCaLamViec {

    @Id
    @Column(name = "ma_phan_cong", length = 20)
    private String maPhanCong;

    // ĐÃ FIX: Bỏ referencedColumnName để Hibernate tự động tìm khóa chính của NhanVien
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_ca")
    private CaLamViec caLamViec;

    @Column(name = "ngay_lam_viec")
    private LocalDate ngayLamViec;
}