package iuh.fit.core.service.impl;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId;
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.core.service.IChiTietHoaDonService;
import iuh.fit.infrastructure.mapper.ChiTietHoaDonMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChiTietHoaDonServiceImpl implements IChiTietHoaDonService {

    private final IChiTietHoaDonRepository chiTietHoaDonRepository;

    public ChiTietHoaDonServiceImpl(IChiTietHoaDonRepository chiTietHoaDonRepository) {
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
    }

    @Override
    public List<ChiTietHoaDonDTO> getAllChiTietHoaDon() {
        return chiTietHoaDonRepository.findAll().stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChiTietHoaDonDTO getChiTietHoaDonById(String maHoaDon, String maDichVu) throws IllegalArgumentException {
        if (maHoaDon == null || maDichVu == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
        return chiTietHoaDonRepository.findById(id)
                .map(ChiTietHoaDonMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ChiTietHoaDonDTO addChiTietHoaDon(ChiTietHoaDonDTO chiTietHoaDonDTO) throws IllegalArgumentException {
        if (chiTietHoaDonDTO.getMaHoaDon() == null || chiTietHoaDonDTO.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (chiTietHoaDonDTO.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        ChiTietHoaDon chiTiet = ChiTietHoaDonMapper.toEntity(chiTietHoaDonDTO);
        ChiTietHoaDonId id = new ChiTietHoaDonId(chiTietHoaDonDTO.getMaHoaDon(), chiTietHoaDonDTO.getMaDichVu());
        chiTiet.setId(id);

        ChiTietHoaDon saved = chiTietHoaDonRepository.save(chiTiet);
        return ChiTietHoaDonMapper.toDTO(saved);
    }

    @Override
    public ChiTietHoaDonDTO updateChiTietHoaDon(ChiTietHoaDonDTO chiTietHoaDonDTO) throws IllegalArgumentException {
        if (chiTietHoaDonDTO.getMaHoaDon() == null || chiTietHoaDonDTO.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (chiTietHoaDonDTO.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        ChiTietHoaDon chiTiet = ChiTietHoaDonMapper.toEntity(chiTietHoaDonDTO);
        ChiTietHoaDonId id = new ChiTietHoaDonId(chiTietHoaDonDTO.getMaHoaDon(), chiTietHoaDonDTO.getMaDichVu());
        chiTiet.setId(id);

        ChiTietHoaDon updated = chiTietHoaDonRepository.update(chiTiet);
        return ChiTietHoaDonMapper.toDTO(updated);
    }

    @Override
    public boolean deleteChiTietHoaDon(String maHoaDon, String maDichVu) {
        try {
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
            chiTietHoaDonRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ChiTietHoaDonDTO> getChiTietHoaDonByHoaDon(String maHoaDon) {
        return chiTietHoaDonRepository.findByHoaDon(maHoaDon).stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    // FIX: thêm phương thức còn thiếu - lấy chi tiết theo mã phiếu đặt
    @Override
    public List<ChiTietHoaDonDTO> getChiTietByMaPhieu(String maPhieu) {
        return chiTietHoaDonRepository.findByHoaDon(maPhieu).stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    // FIX: thêm phương thức còn thiếu - thêm hoặc cập nhật chi tiết hóa đơn
    @Override
    public void addOrUpdateChiTiet(ChiTietHoaDonDTO dto) {
        if (dto == null || dto.getMaHoaDon() == null || dto.getMaDichVu() == null) return;

        ChiTietHoaDonId id = new ChiTietHoaDonId(dto.getMaHoaDon(), dto.getMaDichVu());
        Optional<ChiTietHoaDon> existing = chiTietHoaDonRepository.findById(id);

        ChiTietHoaDon chiTiet = ChiTietHoaDonMapper.toEntity(dto);
        chiTiet.setId(id);

        if (existing.isPresent()) {
            chiTietHoaDonRepository.update(chiTiet);
        } else {
            chiTietHoaDonRepository.save(chiTiet);
        }
    }

    @Override
    public void deleteByHoaDon(String maHoaDon) {
        chiTietHoaDonRepository.deleteByHoaDon(maHoaDon);
    }
}
