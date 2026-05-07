package iuh.fit.core.repository;

import iuh.fit.core.entity.KhuyenMai;

import java.util.List;
import java.util.Optional;

/**
 * Interface: IKhuyenMaiRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Khuyến Mại
 */
public interface IKhuyenMaiRepository {
    /**
     * Tìm khuyến mại theo mã
     */
    Optional<KhuyenMai> findById(String maKhuyenMai);
    
    /**
     * Lấy tất cả khuyến mại
     */
    List<KhuyenMai> findAll();
    
    /**
     * Lưu khuyến mại mới
     */
    KhuyenMai save(KhuyenMai khuyenMai);
    
    /**
     * Cập nhật khuyến mại
     */
    KhuyenMai update(KhuyenMai khuyenMai);
    
    /**
     * Xóa khuyến mại theo mã
     */
    void deleteById(String maKhuyenMai);
}

