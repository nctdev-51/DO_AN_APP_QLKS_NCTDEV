package client.service;

import core.entity.NhanVien;

public interface AuthRemoteService {
    NhanVien login(String username, String password) throws Exception;
}
