// File: iuh/fit/core/entity/YeuCauPheDuyet.java
package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Entity
@Table(name = "YeuCauPheDuyet")
public class YeuCauPheDuyet {

    @Id
    @Column(name = "maYeuCau", length = 50)
    private String maYeuCau;

    @Column(name = "maNhanVien", nullable = false, length = 20)
    private String maNhanVien;

    @Column(name = "tenNhanVien", nullable = false)
    private String tenNhanVien;

    @Column(name = "thoiGianYeuCau", nullable = false)
    private LocalDateTime thoiGianYeuCau;

    @Column(name = "tienDauCa", nullable = false)
    private double tienDauCa;

    @Column(name = "lyDo", columnDefinition = "NVARCHAR(500)")
    private String lyDo;

    @Column(name = "trangThai", nullable = false, length = 20)
    private String trangThai; // CHUA_DUYET, DA_DUYET, TU_CHOI

    @Column(name = "maQuanLy", length = 20)
    private String maQuanLy;

    @Column(name = "thoiGianDuyet")
    private LocalDateTime thoiGianDuyet;
}