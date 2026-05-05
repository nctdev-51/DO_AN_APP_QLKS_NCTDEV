package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    @Id
    @Column(name = "ma_phong", length = 4)
    private String maPhong;

    @Column(name = "ten_phong", length = 100)
    private String tenPhong;

    @Column(name = "gia_phong")
    private Double giaPhong;

    @Column(name = "ma_loai_phong", length = 20, nullable = false)
    private String maLoaiPhong;

    @Column(name = "tinh_trang", length = 50)
    private String tinhTrang;
}