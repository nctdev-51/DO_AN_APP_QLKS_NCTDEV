package iuh.fit.core.service;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import java.time.LocalDate;
import java.util.List;

public interface IPhieuDatPhongService {
    List<PhieuDatPhongDTO> getAllPhieuDatPhong();
    PhieuDatPhongDTO getPhieuDatPhongById(String maPhieu);
    List<PhieuDatPhongDTO> getPhieuDatPhongByKhachHang(String maKhachHang);
    List<PhieuDatPhongDTO> getPhieuDatPhongByPhong(String maPhong);
    List<PhieuDatPhongDTO> getPhieuDatPhongInDateRange(LocalDate startDate, LocalDate endDate);
    PhieuDatPhongDTO addPhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException;
    PhieuDatPhongDTO updatePhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException;
    boolean deletePhieuDatPhong(String maPhieu);

    // Thực thi transaction lưu phiếu
    boolean bookRoomTransaction(PhieuDatPhongDTO phieuDTO);
}