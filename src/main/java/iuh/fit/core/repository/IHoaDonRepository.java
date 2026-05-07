package iuh.fit.core.repository;

import iuh.fit.core.entity.HoaDon;

import java.util.List;
import java.util.Optional;

/**
 * Interface: IHoaDonRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Hóa Đơn
 */
public interface IHoaDonRepository {
    /**
     * Tìm hóa đơn theo mã
     */
    Optional<HoaDon> findById(String maHoaDon);
    
    /**
     * Lấy tất cả hóa đơn
     */
    List<HoaDon> findAll();
    
    /**
     * Lưu hóa đơn mới
     */
    HoaDon save(HoaDon hoaDon);
    
    /**
     * Cập nhật hóa đơn
     */
    HoaDon update(HoaDon hoaDon);
    
    /**
     * Xóa hóa đơn theo mã
     */
    void deleteById(String maHoaDon);
    
    /**
     * Tìm các hóa đơn của khách hàng
     */
    List<HoaDon> findByMaKhachHang(String maKhachHang);
}

