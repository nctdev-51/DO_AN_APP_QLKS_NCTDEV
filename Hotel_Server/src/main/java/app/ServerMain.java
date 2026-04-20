package app;

import server.SocketServer;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        new SocketServer(9999).start();
    }
}
