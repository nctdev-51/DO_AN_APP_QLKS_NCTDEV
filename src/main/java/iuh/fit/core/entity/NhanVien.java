package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @Column(name = "maNhanVien", length = 20)
    private String maNhanVien;

    private String hoTen;

    private LocalDate ngaySinh;

    private boolean gioiTinh;

    private String cccd;

    private String soDienThoai;

    private boolean trangThai;

    @Enumerated(EnumType.STRING)
    private LoaiNhanVien loaiNhanVien;

    private LocalDate ngayVaoLam;

    private String queQuan;
}