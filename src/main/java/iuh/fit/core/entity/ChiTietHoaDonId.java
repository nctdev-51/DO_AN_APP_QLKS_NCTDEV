package iuh.fit.core.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite Primary Key Class: ChiTietHoaDonId
 * 
 * Được dùng để biểu diễn khóa chính ghép (maHoaDon + maDichVu)
 * của bảng ChiTietHoaDon
 * 
 * QUAN TRỌNG: Phải override equals() và hashCode() cho Composite Key
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietHoaDonId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String maHoaDon;
    public String maDichVu;
}

