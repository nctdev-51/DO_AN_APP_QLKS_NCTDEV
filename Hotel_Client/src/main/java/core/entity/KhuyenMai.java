package core.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "KhuyenMai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 5)
    private String maKhuyenMai;

    @Column(nullable = false, length = 50)
    private String tenKhuyenMai;

    @Column(nullable = false)
    private LocalDate ngayBatDau;

    @Column(nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoaiKhuyenMai loaiKhuyenMai;

    @Column(nullable = false)
    private Double chietKhau;
}
