package core.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NhanVien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 5)
    private String maNhanVien;

    @Column(nullable = false, length = 50)
    private String hoTen;

    @Column(nullable = false)
    private LocalDate ngaySinh;

    @Column(nullable = false)
    private Boolean gioiTinh;

    @Column(nullable = false, unique = true, length = 12)
    private String cccd;

    @Column(nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(nullable = false)
    private Boolean trangThai;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoaiNhanVien loaiNhanVien;

    @Column(nullable = false)
    private LocalDate ngayVaoLam;

    @Column(nullable = false, length = 50)
    private String queQuan;

    @OneToMany(mappedBy = "nhanVien")
    private List<PhieuDatPhong> phieuDatPhongs;
}
