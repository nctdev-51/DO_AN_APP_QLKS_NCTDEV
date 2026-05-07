package iuh.fit.core.repository;

import iuh.fit.core.entity.ChiTietPhieuDatPhong;

import java.util.List;

/**
 * Interface: IChiTietPhieuDatPhongRepository (Repository Port)
 * 
 * Tầng: CORE - Repository Layer
 * Trách nhiệm: Định nghĩa hợp đồng để thao tác dữ liệu Chi Tiết Phiếu Đặt Phòng
 */
public interface IChiTietPhieuDatPhongRepository {
    /**
     * Tìm chi tiết phiếu đặt phòng theo mã phiếu
     */
    List<ChiTietPhieuDatPhong> findByMaPhieu(String maPhieu);
    
    /**
     * Lưu chi tiết phiếu đặt phòng
     */
    ChiTietPhieuDatPhong save(ChiTietPhieuDatPhong chiTiet);
    
    /**
     * Xóa chi tiết phiếu đặt phòng theo mã phiếu
     */
    void deleteByMaPhieu(String maPhieu);
}

