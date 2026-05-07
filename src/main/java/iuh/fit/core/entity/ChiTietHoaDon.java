package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: ChiTietHoaDon (Chi tiết hóa đơn)
 * Bảng: ChiTietHoaDon
 * Mô tả: Lưu trữ chi tiết các dịch vụ trong mỗi hóa đơn (Composite Primary Key)
 */
@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {
    @Id
    @Column(name = "maHoaDon")
    private String maHoaDon;

    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    @Column(name = "soLuong")
    private int soLuong;

    // Relationships (mapped but not part of ID)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maHoaDon", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maDichVu", insertable = false, updatable = false)
    private DichVu dichVu;
}
