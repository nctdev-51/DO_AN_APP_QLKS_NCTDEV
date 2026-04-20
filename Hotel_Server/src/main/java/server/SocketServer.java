package server;

import server.handler.ActionDispatcher;
import server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ActionDispatcher dispatcher = new ActionDispatcher();

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Hotel Server is listening on port " + port);
            while (true) {
                Socket client = serverSocket.accept();
                pool.submit(new ClientHandler(client, dispatcher));
            }
        }
    }
}
