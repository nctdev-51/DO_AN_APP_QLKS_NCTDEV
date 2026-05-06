package iuh.fit.core.service;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import java.util.List;

public interface IChiTietHoaDonService {
    List<ChiTietHoaDonDTO> getAllChiTietHoaDon();
    ChiTietHoaDonDTO getChiTietHoaDonById(String maHoaDon, String maDichVu) throws IllegalArgumentException;
    ChiTietHoaDonDTO addChiTietHoaDon(ChiTietHoaDonDTO chiTietHoaDonDTO) throws IllegalArgumentException;
    ChiTietHoaDonDTO updateChiTietHoaDon(ChiTietHoaDonDTO chiTietHoaDonDTO) throws IllegalArgumentException;
    boolean deleteChiTietHoaDon(String maHoaDon, String maDichVu);

    // Lấy chi tiết hóa đơn theo mã hóa đơn
    List<ChiTietHoaDonDTO> getChiTietHoaDonByHoaDon(String maHoaDon);

    // Xóa tất cả chi tiết của 1 hóa đơn
    void deleteByHoaDon(String maHoaDon);
}

