package iuh.fit.core.service.impl;

import iuh.fit.core.dto.CaLamViecDTO;
import iuh.fit.core.entity.CaLamViec;
import iuh.fit.core.service.ICaLamViecService;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class CaLamViecServiceImpl implements ICaLamViecService {
    @Override
    public List<CaLamViecDTO> getAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            List<CaLamViec> list = em.createQuery("SELECT c FROM CaLamViec c", CaLamViec.class).getResultList();
            return list.stream()
                    .map(c -> new CaLamViecDTO(c.getMaCa(), c.getTenCa(), c.getGioBatDau(), c.getGioKetThuc()))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}