package iuh.fit.core.service;

import iuh.fit.core.dto.HoaDonDTO;
import java.time.LocalDate;
import java.util.List;

public interface IHoaDonService {
    List<HoaDonDTO> getHoaDonByDateRange(LocalDate start, LocalDate end);
    double getTotalServiceRevenueByDateRange(LocalDate start, LocalDate end);
    List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieu);
    HoaDonDTO calculateInvoiceAtCheckout(String maPhieu, double thueVAT, double chietKhau);
    HoaDonDTO addHoaDon(HoaDonDTO hoaDon);
}