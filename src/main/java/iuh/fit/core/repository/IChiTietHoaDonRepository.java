package iuh.fit.core.repository;

import iuh.fit.core.entity.ChiTietHoaDon;

import java.util.List;

/**
 * Interface: IChiTietHoaDonRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Chi Tiết Hóa Đơn
 */
public interface IChiTietHoaDonRepository {
    /**
     * Tìm chi tiết hóa đơn theo mã hóa đơn
     */
    List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon);
    
    /**
     * Lưu chi tiết hóa đơn
     */
    ChiTietHoaDon save(ChiTietHoaDon chiTiet);
    
    /**
     * Xóa chi tiết hóa đơn theo mã hóa đơn
     */
    void deleteByMaHoaDon(String maHoaDon);
}

