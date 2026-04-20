package core.service;

import core.entity.KhachHang;

import java.util.List;

public interface KhachHangService {
    List<KhachHang> getAll();

    KhachHang findById(String maKhachHang);

    KhachHang create(KhachHang khachHang);

    KhachHang update(KhachHang khachHang);

    boolean deleteById(String maKhachHang);
}
