package iuh.fit.core.service;

import iuh.fit.core.dto.HoaDonDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface: IHoaDonService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng logic nghiệp vụ cho Hóa Đơn
 */
public interface IHoaDonService {
    /**
     * Tìm hóa đơn theo mã
     */
    Optional<HoaDonDTO> findById(String maHoaDon);
    
    /**
     * Lấy tất cả hóa đơn
     */
    List<HoaDonDTO> findAll();
    
    /**
     * Tạo hóa đơn mới
     */
    HoaDonDTO create(HoaDonDTO dto);
    
    /**
     * Cập nhật hóa đơn
     */
    HoaDonDTO update(HoaDonDTO dto);
    
    /**
     * Xóa hóa đơn
     */
    void delete(String maHoaDon);
    
    /**
     * Tìm hóa đơn theo khách hàng
     */
    List<HoaDonDTO> findByMaKhachHang(String maKhachHang);
}

