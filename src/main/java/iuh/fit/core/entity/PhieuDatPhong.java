package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "phieu_dat_phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {
    @Id
    @Column(name = "ma_phieu_dat", length = 10)
    private String maPhieu;

    @Column(name = "ma_khach_hang", length = 5)
    private String maKhachHang;

    @Column(name = "ma_phong", length = 4)
    private String maPhong;

    @Column(name = "ngay_dat")
    private LocalDate ngayDat;

    @Column(name = "ngay_nhan")
    private LocalDate ngayNhan;

    @Column(name = "ngay_tra")
    private LocalDate ngayTra;

    @Column(name = "tong_tien")
    private double tongTien;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ma_nhan_vien", length = 5)
    private String maNhanVien;
}