package core.service.impl;

import core.entity.KhachHang;
import core.repository.KhachHangRepository;
import core.service.KhachHangService;

import java.util.List;

public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangRepository khachHangRepository = new KhachHangRepository();

    @Override
    public List<KhachHang> getAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public KhachHang findById(String maKhachHang) {
        return khachHangRepository.findById(maKhachHang);
    }

    @Override
    public KhachHang create(KhachHang khachHang) {
        return khachHangRepository.create(khachHang);
    }

    @Override
    public KhachHang update(KhachHang khachHang) {
        return khachHangRepository.update(khachHang);
    }

    @Override
    public boolean deleteById(String maKhachHang) {
        return khachHangRepository.deleteById(maKhachHang);
    }
}
