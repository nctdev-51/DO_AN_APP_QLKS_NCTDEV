package core.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TaiKhoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 5)
    private String maNhanVien;

    @OneToOne
    @MapsId
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @Column(nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(nullable = false, length = 255)
    private String matKhau;

    @Column(nullable = false)
    private Boolean trangThaiTk;
}
