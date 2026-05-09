package iuh.fit.core.service.impl;

import iuh.fit.core.dto.*;
import iuh.fit.core.entity.*;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.core.repository.ILichSuCaLamViecRepository;
import iuh.fit.core.repository.IPhanCongRepository;
import iuh.fit.core.service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class GiaoCaServiceImpl implements IGiaoCaService {

    private final ILichSuCaLamViecRepository lichSuRepo;
    private final IPhanCongRepository phanCongRepo;
    private final IHoaDonRepository hoaDonRepo;

    public GiaoCaServiceImpl(ILichSuCaLamViecRepository lichSuRepo, IPhanCongRepository phanCongRepo, IHoaDonRepository hoaDonRepo) {
        this.lichSuRepo = lichSuRepo;
        this.phanCongRepo = phanCongRepo;
        this.hoaDonRepo = hoaDonRepo;
    }

    // --- HÀM HELPER CHUYỂN ENTITY SANG DTO (Thay thế cho Mapper) ---
    private LichSuCaLamViecDTO toDTO(LichSuCaLamViec entity) {
        if (entity == null) return null;
        return new LichSuCaLamViecDTO(
                entity.getMaLichSu(),
                entity.getNhanVien() != null ? entity.getNhanVien().getMaNhanVien() : entity.getMaNhanVien(),
                entity.getNhanVien() != null ? entity.getNhanVien().getHoTen() : null,
                entity.getCaLamViec() != null ? entity.getCaLamViec().getMaCa() : entity.getMaCa(),
                entity.getCaLamViec() != null ? entity.getCaLamViec().getTenCa() : null,
                entity.getThoiGianNhanCa(),
                entity.getThoiGianGiaoCa(),
                entity.getTienDauCa(),
                entity.getTongThuTrongCa(),
                entity.getTienCuoiCaThucTe(),
                entity.getTienChenhLech(),
                entity.getGhiChu(),
                entity.getTrangThai()
        );
    }

    @Override
    public LichSuCaLamViecDTO nhanCa(String maNhanVien, double tienDauCa, boolean isManagerOverride) throws Exception {
        PhanCongCaLamViec pc = phanCongRepo.findPhanCongToday(maNhanVien, LocalDate.now());
        if (pc == null) {
            throw new Exception("⛔ Bạn không được phân công làm việc vào hôm nay!");
        }

        LocalTime now = LocalTime.now();
        LocalTime gioBatDau = pc.getCaLamViec().getGioBatDau();
        long minutesDiff = ChronoUnit.MINUTES.between(gioBatDau, now);

        if (!isManagerOverride) {
            if (minutesDiff < -30) {
                throw new Exception("⏳ Chưa đến giờ! Bạn chỉ được nhận ca sớm tối đa 30 phút.");
            }
            if (minutesDiff > 30) {
                throw new Exception("🚨 Bạn đã ĐẾN TRỄ quá 30 phút! Hệ thống đã khóa. Vui lòng liên hệ Quản lý.");
            }
        }

        LichSuCaLamViec caCu = lichSuRepo.findCaDangLam(maNhanVien);
        if (caCu != null) {
            throw new Exception("⛔ Bạn vẫn còn ca trước đó chưa bàn giao. Hãy chốt ca cũ trước!");
        }

        LichSuCaLamViec ls = new LichSuCaLamViec();
        ls.setMaLichSu("LS_" + System.currentTimeMillis());
        ls.setNhanVien(pc.getNhanVien());
        ls.setCaLamViec(pc.getCaLamViec());
        ls.setThoiGianNhanCa(LocalDateTime.now());
        ls.setTienDauCa(tienDauCa);
        ls.setTrangThai("DANG_LAM");

        return toDTO(lichSuRepo.save(ls));
    }

    @Override
    public LichSuCaLamViecDTO giaoCa(String maLichSu, double tienCuoiCaThucTe, String ghiChu) throws Exception {
        LichSuCaLamViec ls = lichSuRepo.findById(maLichSu).orElseThrow(() -> new Exception("Không tìm thấy ca làm việc!"));

        double tongThu = hoaDonRepo.sumDoanhThuByNhanVienAndTime(ls.getNhanVien().getMaNhanVien(), ls.getThoiGianNhanCa(), LocalDateTime.now());

        double tienLyThuyet = ls.getTienDauCa() + tongThu;
        double chenhLech = tienCuoiCaThucTe - tienLyThuyet;

        if (chenhLech != 0 && (ghiChu == null || ghiChu.trim().isEmpty())) {
            String msg = chenhLech > 0 ? "dư" : "thiếu";
            throw new Exception("⚠️ Tiền trong két đang " + msg + " " + String.format("%,.0f đ", Math.abs(chenhLech)) + " so với hệ thống. BẮT BUỘC phải nhập lý do/ghi chú!");
        }

        ls.setThoiGianGiaoCa(LocalDateTime.now());
        ls.setTongThuTrongCa(tongThu);
        ls.setTienCuoiCaThucTe(tienCuoiCaThucTe);
        ls.setTienChenhLech(chenhLech);
        ls.setGhiChu(ghiChu);
        ls.setTrangThai("DA_GIAO_CA");

        return toDTO(lichSuRepo.update(ls));
    }

    @Override
    public LichSuCaLamViecDTO getCaDangLam(String maNhanVien) {
        LichSuCaLamViec entity = lichSuRepo.findCaDangLam(maNhanVien);
        return entity != null ? toDTO(entity) : null;
    }
}