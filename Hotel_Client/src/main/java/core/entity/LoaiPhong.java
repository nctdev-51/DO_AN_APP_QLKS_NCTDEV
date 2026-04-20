package core.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LoaiPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhong implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 20)
    private String maLoaiPhong;

    @Column(nullable = false, length = 50)
    private String tenLoaiPhong;

    @Column(nullable = false, length = 50)
    private String moTa;

    @OneToMany(mappedBy = "loaiPhong")
    private List<Phong> phongs;
}
