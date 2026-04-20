package client.service;

import dto.KhachHangDTO;

import java.util.List;

public interface KhachHangRemoteService {
    List<KhachHangDTO> getAllKhachHang() throws Exception;

    KhachHangDTO searchByMa(String maKhachHang) throws Exception;

    boolean createKhachHang(KhachHangDTO dto) throws Exception;

    boolean updateKhachHang(KhachHangDTO dto) throws Exception;

    boolean deleteKhachHang(String maKhachHang) throws Exception;
}
