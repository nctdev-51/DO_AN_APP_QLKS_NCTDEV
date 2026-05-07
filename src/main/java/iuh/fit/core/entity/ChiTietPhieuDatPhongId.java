package iuh.fit.core.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite Primary Key Class: ChiTietPhieuDatPhongId
 * 
 * Được dùng để biểu diễn khóa chính ghép (maPhieu + maDichVu)
 * của bảng ChiTietPhieuDatPhong
 * 
 * QUAN TRỌNG: Phải override equals() và hashCode() cho Composite Key
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietPhieuDatPhongId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String maPhieu;
    public String maDichVu;
}

