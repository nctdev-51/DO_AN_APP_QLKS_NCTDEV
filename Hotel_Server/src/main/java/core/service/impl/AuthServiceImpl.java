package core.service.impl;

import core.entity.NhanVien;
import core.repository.TaiKhoanRepository;
import core.service.AuthService;
import shared.model.LoginRequestData;

public class AuthServiceImpl implements AuthService {
    private final TaiKhoanRepository taiKhoanRepository = new TaiKhoanRepository();

    @Override
    public NhanVien login(LoginRequestData loginRequest) {
        return taiKhoanRepository.findNhanVienByCredentials(loginRequest.getUsername(), loginRequest.getPassword());
    }
}
