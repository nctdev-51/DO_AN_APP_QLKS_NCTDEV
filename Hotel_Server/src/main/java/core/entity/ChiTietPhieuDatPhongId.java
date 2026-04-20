package core.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuDatPhongId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(length = 10)
    private String maPhieu;

    @Column(length = 20)
    private String maDichVu;
}
