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
    public ChiTietHoaDonDTO getChiTietHoaDonById(String maHoaDon, String maDichVu) {
        if (maHoaDon == null || maDichVu == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        ChiTietHoaDonId id = new ChiTietHoaDonId(maHoaDon, maDichVu);
        return chiTietHoaDonRepository.findById(id)
                .map(ChiTietHoaDonMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ChiTietHoaDonDTO addChiTietHoaDon(ChiTietHoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (dto.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Dùng Mapper để chuyển đổi, mapper đã tự động tạo Khóa ghép và set Số lượng
        ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);

        ChiTietHoaDon saved = chiTietHoaDonRepository.save(entity);
        return ChiTietHoaDonMapper.toDTO(saved);
    }

    @Override
    public ChiTietHoaDonDTO updateChiTietHoaDon(ChiTietHoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaDichVu() == null) {
            throw new IllegalArgumentException("Mã hóa đơn và mã dịch vụ không được trống");
        }
        if (dto.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        // Mapper đã tự động map ID ghép và Số lượng, không cần gọi lại các hàm set lỗi
        ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);

        ChiTietHoaDon updated = chiTietHoaDonRepository.update(entity);
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

    @Override
    public List<ChiTietHoaDonDTO> getChiTietByMaPhieu(String maPhieu) {
        // Tạm thời dùng findByHoaDon (cần sửa logic thực tế sau)
        return chiTietHoaDonRepository.findByHoaDon(maPhieu).stream()
                .map(ChiTietHoaDonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addOrUpdateChiTiet(ChiTietHoaDonDTO dto) {
        if (dto == null || dto.getMaHoaDon() == null || dto.getMaDichVu() == null) return;

        ChiTietHoaDonId id = new ChiTietHoaDonId(dto.getMaHoaDon(), dto.getMaDichVu());
        Optional<ChiTietHoaDon> existing = chiTietHoaDonRepository.findById(id);

        // Mapper đã xử lý việc set ID ghép và setSoLuong
        ChiTietHoaDon entity = ChiTietHoaDonMapper.toEntity(dto);

        if (existing.isPresent()) {
            chiTietHoaDonRepository.update(entity);
        } else {
            chiTietHoaDonRepository.save(entity);
        }
    }

    @Override
    public void deleteByHoaDon(String maHoaDon) {
        chiTietHoaDonRepository.deleteByHoaDon(maHoaDon);
    }
}