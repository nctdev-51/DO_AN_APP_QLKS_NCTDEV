package iuh.fit.core.service;

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

    // Hóa đơn específicas
    List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieuDat);

    // Tính toán hóa đơn khi trả phòng
    // Công thức: tổng tiền = (số ngày ở * giá phòng) + tổng dịch vụ + VAT - chiết khấu
    HoaDonDTO calculateInvoiceAtCheckout(String maPhieuDat, double thueVAT, double chietKhau) throws IllegalArgumentException;

    // Thống kê doanh thu
    List<HoaDonDTO> getHoaDonByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalServiceRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}

