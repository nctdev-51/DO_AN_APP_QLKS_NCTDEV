package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    // ĐÃ FIX: Tối giản hóa cấu hình khóa ngoại
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phieu_dat")
    private PhieuDatPhong phieuDatPhong;

    @Column(name = "tong_tien")
    private double tongTien;

    @Column(name = "trang_thai_thanh_toan")
    private String trangThaiThanhToan;
}