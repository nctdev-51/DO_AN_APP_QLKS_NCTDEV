package client.service;

import client.network.SocketClient;
import core.entity.NhanVien;
import shared.model.LoginRequestData;
import shared.model.RequestObject;
import shared.model.ResponseObject;

public class AuthRemoteServiceImpl implements AuthRemoteService {
    private final SocketClient socketClient;

    public AuthRemoteServiceImpl(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    @Override
    public NhanVien login(String username, String password) throws Exception {
        ResponseObject response = socketClient.sendRequest(
                new RequestObject("AUTH_LOGIN", new LoginRequestData(username, password))
        );
        if (response == null || !response.isSuccess()) {
            throw new IllegalStateException(response == null ? "Khong co phan hoi tu server." : response.getMessage());
        }
        return (NhanVien) response.getData();
    }
}
