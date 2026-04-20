package client.service;

import client.network.SocketClient;
import core.entity.KhachHang;
import core.entity.LoaiKhachHang;
import dto.KhachHangDTO;
import shared.model.RequestObject;
import shared.model.ResponseObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhachHangRemoteServiceImpl implements KhachHangRemoteService {
    private final SocketClient socketClient;

    public KhachHangRemoteServiceImpl(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public List<KhachHangDTO> getAllKhachHang() throws Exception {
        ResponseObject response = socketClient.sendRequest(new RequestObject("KHACH_HANG_GET_ALL", null));
        ensureSuccess(response);
        List<KhachHangDTO> result = new ArrayList<>();
        if (response.getData() instanceof List<?> list) {
            for (Object item : list) {
                result.add(toDto(item));
            }
        }
        return result;
    }

    @Override
    public KhachHangDTO searchByMa(String maKhachHang) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("maKhachHang", maKhachHang);
        ResponseObject response = socketClient.sendRequest(new RequestObject("KHACH_HANG_SEARCH_BY_MA", payload));
        ensureSuccess(response);
        return response.getData() == null ? null : toDto(response.getData());
    }

    @Override
    public boolean createKhachHang(KhachHangDTO dto) throws Exception {
        ResponseObject response = socketClient.sendRequest(new RequestObject("KHACH_HANG_CREATE", toEntity(dto)));
        ensureSuccess(response);
        return true;
    }

    @Override
    public boolean updateKhachHang(KhachHangDTO dto) throws Exception {
        ResponseObject response = socketClient.sendRequest(new RequestObject("KHACH_HANG_UPDATE", toEntity(dto)));
        ensureSuccess(response);
        return true;
    }

    @Override
    public boolean deleteKhachHang(String maKhachHang) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("maKhachHang", maKhachHang);
        ResponseObject response = socketClient.sendRequest(new RequestObject("KHACH_HANG_DELETE", payload));
        ensureSuccess(response);
        return true;
    }

    private void ensureSuccess(ResponseObject response) {
        if (response == null || !response.isSuccess()) {
            throw new IllegalStateException(response == null ? "Khong co phan hoi tu server." : response.getMessage());
        }
    }

    private KhachHangDTO toDto(Object data) {
        if (data instanceof KhachHang kh) {
            return new KhachHangDTO(
                    kh.getMaKhachHang(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getNgaySinh(),
                    kh.getLoaiKhachHang().name()
            );
        }
        throw new IllegalStateException("Dinh dang du lieu khach hang khong hop le.");
    }

    private KhachHang toEntity(KhachHangDTO dto) {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(dto.getMaKhachHang());
        kh.setHoTen(dto.getHoTen());
        kh.setSoDienThoai(dto.getSoDienThoai());
        kh.setNgaySinh(dto.getNgaySinh() == null ? LocalDate.now() : dto.getNgaySinh());
        kh.setLoaiKhachHang(LoaiKhachHang.valueOf(dto.getLoaiKhachHang()));
        return kh;
    }
}
