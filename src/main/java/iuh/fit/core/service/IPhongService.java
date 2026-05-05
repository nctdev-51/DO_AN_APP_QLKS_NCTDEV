package iuh.fit.core.service;

import iuh.fit.core.dto.PhongDTO;
import java.time.LocalDate;
import java.util.List;

public interface IPhongService {
    List<PhongDTO> getAllPhong();
    PhongDTO getPhongById(String maPhong);
    List<PhongDTO> getPhongByTinhTrang(String tinhTrang);
    List<PhongDTO> getPhongByMaLoaiPhong(String maLoaiPhong);
    PhongDTO addPhong(PhongDTO phongDTO) throws IllegalArgumentException;
    PhongDTO updatePhong(PhongDTO phongDTO) throws IllegalArgumentException;
    boolean deletePhong(String maPhong);

    List<PhongDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice);
}