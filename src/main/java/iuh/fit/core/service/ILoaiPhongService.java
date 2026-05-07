package iuh.fit.core.service;

import iuh.fit.core.dto.LoaiPhongDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interface: ILoaiPhongService (Service Port)
 * 
 * Tầng: CORE - Service Layer
 * Trách nhiệm: Định nghĩa hợp đồng logic nghiệp vụ cho Loại Phòng
 */
public interface ILoaiPhongService {
    /**
     * Tìm loại phòng theo mã
     */
    Optional<LoaiPhongDTO> findById(String maLoaiPhong);
    
    /**
     * Lấy tất cả loại phòng
     */
    List<LoaiPhongDTO> findAll();
    
    /**
     * Tạo loại phòng mới
     */
    LoaiPhongDTO create(LoaiPhongDTO dto);
    
    /**
     * Cập nhật loại phòng
     */
    LoaiPhongDTO update(LoaiPhongDTO dto);
    
    /**
     * Xóa loại phòng
     */
    void delete(String maLoaiPhong);
}

