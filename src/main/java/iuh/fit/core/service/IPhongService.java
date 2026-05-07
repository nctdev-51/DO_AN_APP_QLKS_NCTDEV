package iuh.fit.core.service;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;
import iuh.fit.infrastructure.mapper.PhongMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public interface IPhongService {
    List<PhongDTO> getAllPhong();
    PhongDTO getPhongById(String maPhong);
    List<PhongDTO> getPhongByTinhTrang(String tinhTrang);
    List<PhongDTO> getPhongByMaLoaiPhong(String maLoaiPhong);
    PhongDTO addPhong(PhongDTO phongDTO) throws IllegalArgumentException;
    PhongDTO updatePhong(PhongDTO phongDTO) throws IllegalArgumentException;
    boolean deletePhong(String maPhong);

    List<PhongDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice);
    // Thêm vào file IPhongService.java
    List<PhongDTO> getPhongByPhieuDat(String maPhieu);

    List<Phong> findPhongByMaPhieu(String maPhieu);
}