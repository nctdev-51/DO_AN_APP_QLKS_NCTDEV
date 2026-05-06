package iuh.fit.core.service.impl;
import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.entity.DichVu;
import iuh.fit.core.repository.IDichVuRepository;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.infrastructure.mapper.DichVuMapper;
import java.util.List;
import java.util.stream.Collectors;
public class DichVuServiceImpl implements IDichVuService {
    private final IDichVuRepository dichVuRepository;
    public DichVuServiceImpl(IDichVuRepository dichVuRepository) {
        this.dichVuRepository = dichVuRepository;
    }
    @Override
    public List<DichVuDTO> getAllDichVu() {
        return dichVuRepository.findAll().stream()
            .map(DichVuMapper::entityToDTO)
            .collect(Collectors.toList());
    }
    @Override
    public DichVuDTO getDichVuById(String maDichVu) {
        return dichVuRepository.findById(maDichVu)
            .map(DichVuMapper::entityToDTO)
            .orElse(null);
    }
    @Override
    public DichVuDTO addDichVu(DichVuDTO dichVuDTO) throws IllegalArgumentException {
        if (dichVuDTO.getMaDichVu() == null || dichVuDTO.getMaDichVu().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ không được để trống");
        }
        DichVu entity = DichVuMapper.dtoToEntity(dichVuDTO);
        DichVu saved = dichVuRepository.save(entity);
        return DichVuMapper.entityToDTO(saved);
    }
    @Override
    public DichVuDTO updateDichVu(DichVuDTO dichVuDTO) throws IllegalArgumentException {
        if (dichVuDTO.getMaDichVu() == null || dichVuDTO.getMaDichVu().isEmpty()) {
            throw new IllegalArgumentException("Mã dịch vụ không được để trống");
        }
        DichVu entity = DichVuMapper.dtoToEntity(dichVuDTO);
        DichVu updated = dichVuRepository.update(entity);
        return DichVuMapper.entityToDTO(updated);
    }
    @Override
    public boolean deleteDichVu(String maDichVu) {
        try {
            dichVuRepository.deleteById(maDichVu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
