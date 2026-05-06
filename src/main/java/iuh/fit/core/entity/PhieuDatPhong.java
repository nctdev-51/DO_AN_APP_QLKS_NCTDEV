package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "PhieuDatPhong") // Đã sửa tên bảng cho khớp DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {

    @Id
    @Column(name = "maPhieu", length = 10) // Sửa lại đúng với DB
    private String maPhieu;

    @Column(name = "maKhachHang", length = 5) // Sửa lại đúng với DB
    private String maKhachHang;

    @Column(name = "maPhong", length = 4) // Sửa lại đúng với DB
    private String maPhong;

    @Column(name = "ngayDat") // Sửa lại đúng với DB
    private LocalDate ngayDat;

    @Column(name = "ngayNhan") // Sửa lại đúng với DB
    private LocalDate ngayNhan;

    @Column(name = "ngayTra") // Sửa lại đúng với DB
    private LocalDate ngayTra;

    @Column(name = "tongTien")
    private Double tongTien; // Sửa chữ 'd' thành 'D' hoa

    @Column(name = "trangThai", length = 30) // Sửa lại đúng với DB
    private String trangThai;

    @Column(name = "maNhanVien", length = 5) // Sửa lại đúng với DB
    private String maNhanVien;
}