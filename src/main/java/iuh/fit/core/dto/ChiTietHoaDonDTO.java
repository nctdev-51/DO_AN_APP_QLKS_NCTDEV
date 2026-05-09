package iuh.fit.core.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maHoaDon;
    private String maDichVu;
    private String tenDichVu;
    private int soLuong;
    private double giaTienTungDichVu;
    private double thanhTien;
    private String maPhieu;  // dùng trong GoiDichVuController để lưu


}
