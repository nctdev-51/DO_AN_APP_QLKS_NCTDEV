package core.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ChiTietPhieuDatPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuDatPhong implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ChiTietPhieuDatPhongId id;

    @ManyToOne
    @MapsId("maPhieu")
    @JoinColumn(name = "maPhieu")
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne
    @MapsId("maDichVu")
    @JoinColumn(name = "maDichVu")
    private DichVu dichVu;

    private Integer soLuong;

    @Column(length = 500)
    private String ghiChu;
}
