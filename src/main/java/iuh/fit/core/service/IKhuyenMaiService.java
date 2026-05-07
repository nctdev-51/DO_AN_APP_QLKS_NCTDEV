package iuh.fit.core.service;

import iuh.fit.core.dto.KhuyenMaiDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface: IKhuyenMaiService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng logic nghiệp vụ cho Khuyến Mại
 */
public interface IKhuyenMaiService {
    /**
     * Tìm khuyến mại theo mã
     */
    Optional<KhuyenMaiDTO> findById(String maKhuyenMai);
    
    /**
     * Lấy tất cả khuyến mại
     */
    List<KhuyenMaiDTO> findAll();
    
    /**
     * Tạo khuyến mại mới
     */
    KhuyenMaiDTO create(KhuyenMaiDTO dto);
    
    /**
     * Cập nhật khuyến mại
     */
    KhuyenMaiDTO update(KhuyenMaiDTO dto);
    
    /**
     * Xóa khuyến mại
     */
    void delete(String maKhuyenMai);
}

