package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Phong") // Đổi thành chữ "P" hoa cho khớp chính xác với script SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phong {

    @Id
    @Column(name = "maPhong", length = 4) // Đã sửa tên cột
    private String maPhong;

    @Column(name = "tenPhong", length = 100) // Đã sửa tên cột
    private String tenPhong;

    @Column(name = "giaPhong") // Đã sửa tên cột
    private Double giaPhong;

    @Column(name = "maLoaiPhong", length = 20, nullable = false) // Đã sửa tên cột
    private String maLoaiPhong;

    @Column(name = "tinhTrang", length = 50) // Đã sửa tên cột
    private String tinhTrang;
}