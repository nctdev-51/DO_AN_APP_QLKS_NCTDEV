package iuh.fit.core.service.impl;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.ChiTietHoaDonDTO;
import java.time.LocalDate;
import java.util.List;

public interface IHoaDonService {
    List<HoaDonDTO> getAllHoaDon();
    HoaDonDTO getHoaDonById(String maHoaDon);
    HoaDonDTO addHoaDon(HoaDonDTO hoaDonDTO) throws IllegalArgumentException;
    HoaDonDTO updateHoaDon(HoaDonDTO hoaDonDTO) throws IllegalArgumentException;
    boolean deleteHoaDon(String maHoaDon);

    // FIX: xóa khai báo trùng, chỉ giữ 1 phương thức getHoaDonByPhieuDat
    List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieuDat);

    // Tính toán hóa đơn khi trả phòng
    HoaDonDTO calculateInvoiceAtCheckout(String maPhieuDat, double thueVAT, double chietKhau) throws IllegalArgumentException;

    // Thống kê doanh thu
    List<HoaDonDTO> getHoaDonByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalServiceRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}
