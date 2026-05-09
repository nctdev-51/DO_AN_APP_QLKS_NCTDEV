package iuh.fit.core.service;

import iuh.fit.core.dto.LichSuCaLamViecDTO;

public interface IGiaoCaService {
    LichSuCaLamViecDTO nhanCa(String maNhanVien, double tienDauCa, boolean isManagerOverride) throws Exception;

    LichSuCaLamViecDTO giaoCa(String maLichSu, double tienCuoiCaThucTe, String ghiChu) throws Exception;

    LichSuCaLamViecDTO getCaDangLam(String maNhanVien);
}