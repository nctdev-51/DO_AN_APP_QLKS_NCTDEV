package core.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phong implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 4)
    private String maPhong;

    @Column(length = 100)
    private String tenPhong;

    @Column(nullable = false)
    private Double giaPhong;

    @ManyToOne
    @JoinColumn(name = "maLoaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TinhTrangPhong tinhTrang;
}
