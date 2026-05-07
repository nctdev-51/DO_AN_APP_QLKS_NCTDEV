package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: ChiTietPhieuDatPhong (Chi tiết phiếu đặt phòng)
 * Bảng: ChiTietPhieuDatPhong
 * Mô tả: Lưu trữ chi tiết các dịch vụ trong mỗi phiếu đặt phòng (Composite Primary Key)
 */
@Entity
@Table(name = "ChiTietPhieuDatPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChiTietPhieuDatPhongId.class)
public class ChiTietPhieuDatPhong {
    @Id
    @Column(name = "maPhieu", length = 10)
    private String maPhieu;

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "ghiChu", length = 500)
    private String ghiChu;

    // Relationships (mapped but not part of ID)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maPhieu", insertable = false, updatable = false)
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;
}
