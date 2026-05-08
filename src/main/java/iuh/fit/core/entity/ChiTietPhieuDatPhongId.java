package iuh.fit.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable // BẮT BUỘC PHẢI CÓ
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhieuDatPhongId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "maPhieu", length = 30)
    public String maPhieu;

    @Column(name = "maDichVu", length = 20)
    public String maDichVu;
}