package iuh.fit.core.repository;

import iuh.fit.core.entity.LoaiPhong;

import java.util.List;
import java.util.Optional;

/**
 * Interface: ILoaiPhongRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Loại Phòng
 */
public interface ILoaiPhongRepository {
    /**
     * Tìm loại phòng theo mã
     */
    Optional<LoaiPhong> findById(String maLoaiPhong);
    
    /**
     * Lấy tất cả loại phòng
     */
    List<LoaiPhong> findAll();
    
    /**
     * Lưu loại phòng mới
     */
    LoaiPhong save(LoaiPhong loaiPhong);
    
    /**
     * Cập nhật loại phòng
     */
    LoaiPhong update(LoaiPhong loaiPhong);
    
    /**
     * Xóa loại phòng theo mã
     */
    void deleteById(String maLoaiPhong);
}

