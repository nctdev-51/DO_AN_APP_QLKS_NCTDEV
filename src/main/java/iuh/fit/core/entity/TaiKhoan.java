package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TaiKhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {
    @Id
    @Column(name = "maNhanVien", length = 5)
    private String maNhanVien;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien", insertable = false, updatable = false)
    private NhanVien nhanVien;

    @Column(name = "tenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "matKhau", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "trangThaiTK", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean trangThaiTK;
}