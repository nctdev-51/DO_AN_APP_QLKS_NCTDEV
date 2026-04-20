package server.handler;

import shared.model.RequestObject;
import shared.model.ResponseObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ActionDispatcher actionDispatcher;

    public ClientHandler(Socket socket, ActionDispatcher actionDispatcher) {
        this.socket = socket;
        this.actionDispatcher = actionDispatcher;
    }

    @Override
    public void run() {
        try (socket;
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            RequestObject request = (RequestObject) input.readObject();
            ResponseObject response = actionDispatcher.dispatch(request);
            output.writeObject(response);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
