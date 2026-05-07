package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: LoaiPhong (Loại phòng)
 * Bảng: LoaiPhong
 * Mô tả: Lưu trữ thông tin các loại phòng (Đơn, Đôi, Gia đình, VIP)
 */
@Entity
@Table(name = "LoaiPhong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhong {
    @Id
    @Column(name = "maLoaiPhong", length = 20)
    private String maLoaiPhong;

    @Column(name = "tenLoaiPhong", length = 50, nullable = false)
    private String tenLoaiPhong;

    @Column(name = "moTa", length = 50, nullable = false)
    private String moTa;
}