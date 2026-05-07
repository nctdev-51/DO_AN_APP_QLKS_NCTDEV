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

    @Column(name = "giaPhong")
    private double giaPhong;

    @Column(name = "maLoaiPhong", length = 20, insertable = false, updatable = false)
    private String maLoaiPhong;  // Mã loại phòng để query

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maLoaiPhong")
    private LoaiPhong loaiPhong;  // Quan hệ entity

    @Column(name = "tinhTrang", length = 50)
    private String tinhTrang;
}