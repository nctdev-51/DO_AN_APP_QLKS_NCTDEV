package iuh.fit.core.service.impl;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.repository.*;
import iuh.fit.core.service.IHoaDonService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonServiceImpl implements IHoaDonService {

    private final IHoaDonRepository hoaDonRepository;
    private final IChiTietHoaDonRepository chiTietHDRepository;
    private final IPhieuDatPhongRepository phieuDatPhongRepository;
    private final IPhongRepository phongRepository;

    public HoaDonServiceImpl(IHoaDonRepository hoaDonRepository,
                             IChiTietHoaDonRepository chiTietHDRepository,
                             IPhieuDatPhongRepository phieuDatPhongRepository,
                             IPhongRepository phongRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHDRepository = chiTietHDRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.phongRepository = phongRepository;
    }

    @Override
    public List<HoaDonDTO> getHoaDonByDateRange(LocalDate start, LocalDate end) {
        return new ArrayList<>();
    }

    @Override
    public double getTotalServiceRevenueByDateRange(LocalDate start, LocalDate end) {
        return 0.0;
    }

    @Override
    public List<HoaDonDTO> getHoaDonByPhieuDat(String maPhieu) {
        return new ArrayList<>();
    }

    @Override
    public HoaDonDTO calculateInvoiceAtCheckout(String maPhieu, double thueVAT, double chietKhau) {
        return new HoaDonDTO();
    }

    @Override
    public HoaDonDTO addHoaDon(HoaDonDTO hoaDon) {
        return hoaDon;
    }
}