package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity: NhanVien (Nhân Viên)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Nhân Viên với các thuộc tính và relationship
 * 
 * @Entity: Đánh dấu lớp này là một JPA Entity, sẽ được map vào bảng "nhan_vien" trong DB
 * Mỗi instance của lớp này tương ứng với một record trong bảng
 */
@Entity
@Table(name = "nhan_vien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    
    @Id
    @Column(name = "ma_nhan_vien", length = 20)
    private String maNhanVien;
    
    @Column(name = "ho_ten", length = 100, nullable = false)
    private String hoTen;
    
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    
    @Column(name = "gioi_tinh")
    private boolean gioiTinh; // true = Nam, false = Nữ
    
    @Column(name = "cccd", length = 20, unique = true)
    private String cccd;
    
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;
    
    @Column(name = "trang_thai")
    private boolean trangThai; // true = Active, false = Inactive
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_nhan_vien")
    private LoaiNhanVien loaiNhanVien;
    
    @Column(name = "ngay_vao_lam")
    private LocalDate ngayVaoLam;
    
    @Column(name = "que_quan", length = 200)
    private String queQuan;
}

