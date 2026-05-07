package iuh.fit.core.service.impl;

import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.entity.DichVu;
import iuh.fit.core.repository.IDichVuRepository;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.infrastructure.mapper.DichVuMapper;
import java.util.List;
import java.util.stream.Collectors;

public class DichVuServiceImpl implements IDichVuService {
    private final IDichVuRepository repository;

    public DichVuServiceImpl(IDichVuRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DichVuDTO> getAllDichVu() {
        return repository.findAll().stream()
                .map(DichVuMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DichVuDTO getDichVuById(String maDichVu) {
        return repository.findById(maDichVu)
                .map(DichVuMapper::entityToDTO)
                .orElse(null);
    }

    @Override
    public DichVuDTO addDichVu(DichVuDTO dichVuDTO) {
        DichVu entity = DichVuMapper.dtoToEntity(dichVuDTO);
        return DichVuMapper.entityToDTO(repository.save(entity));
    }

    @Override
    public DichVuDTO updateDichVu(DichVuDTO dichVuDTO) {
        DichVu entity = DichVuMapper.dtoToEntity(dichVuDTO);
        return DichVuMapper.entityToDTO(repository.update(entity));
    }

    @Override
    public boolean deleteDichVu(String maDichVu) {
        try {
            repository.deleteById(maDichVu);
            return true;
        } catch (Exception e) { return false; }
    }
}