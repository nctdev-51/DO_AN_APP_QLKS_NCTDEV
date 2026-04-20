package core.service;

import core.entity.NhanVien;
import shared.model.LoginRequestData;

public interface AuthService {
    NhanVien login(LoginRequestData loginRequest);
}
