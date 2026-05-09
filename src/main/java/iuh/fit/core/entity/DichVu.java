package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Entity
@Table(name = "DichVu") // 👉 Sửa thành DichVu
public class DichVu {

    @Id
    @Column(name = "maDichVu", length = 20) // 👉 Sửa thành maDichVu
    private String maDichVu;

    @Column(name = "tenDichVu", length = 100, nullable = false) // 👉 Sửa thành tenDichVu
    private String tenDichVu;

    @Column(name = "giaTien") // 👉 Sửa thành giaTien
    private double giaTien;

    @Column(name = "moTa", length = 255) // 👉 Sửa thành moTa
    private String moTa;

}