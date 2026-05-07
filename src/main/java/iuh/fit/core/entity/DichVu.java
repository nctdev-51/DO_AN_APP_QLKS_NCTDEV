package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: DichVu (Dịch vụ)
 * Bảng: DichVu
 * Mô tả: Lưu trữ thông tin các dịch vụ mà khách sạn cung cấp (nước uống, giặt, ăn sáng,...)
 */
@Entity
@Table(name = "DichVu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {
    @Id
    @Column(name = "maDichVu", length = 20)
    private String maDichVu;

    @Column(name = "tenDichVu", length = 100, nullable = false)
    private String tenDichVu;

    @Column(name = "giaTien", nullable = false)
    private double giaTien;

    @Column(name = "moTa", length = 255)
    private String moTa;

}