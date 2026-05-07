package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: Phong (Phòng)
 * Bảng: Phong
 * Mô tả: Lưu trữ thông tin chi tiết của các phòng khách sạn
 */
@Entity
@Table(name = "Phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    @Id
    @Column(name = "maPhong", length = 4)
    private String maPhong;

    @Column(name = "tenPhong", length = 100)
    private String tenPhong;

    @Column(name = "giaPhong", nullable = false)
    private double giaPhong;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maLoaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "tinhTrang", length = 50, nullable = false)
    private String tinhTrang; // 'Trống', 'Đã Đặt', 'Bảo Trì'
}