package core.entity;

import java.io.Serializable;

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
@Table(name = "ChiTietHoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ChiTietHoaDonId id;

    @ManyToOne
    @MapsId("maHoaDon")
    @JoinColumn(name = "maHoaDon")
    private HoaDon hoaDon;

    @ManyToOne
    @MapsId("maDichVu")
    @JoinColumn(name = "maDichVu")
    private DichVu dichVu;

    private Integer soLuong;
}
