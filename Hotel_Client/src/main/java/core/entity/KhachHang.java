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
@Table(name = "KhachHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 5)
    private String maKhachHang;

    @Column(nullable = false, length = 50)
    private String hoTen;

    @Column(nullable = false, unique = true, length = 10)
    private String soDienThoai;

    @Column(nullable = false)
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoaiKhachHang loaiKhachHang;

    @OneToMany(mappedBy = "khachHang")
    private List<PhieuDatPhong> phieuDatPhongs;
}
