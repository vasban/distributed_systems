package master;

import constant.Constants;
import model.Request;
import worker.WorkerHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MasterReducerListener extends Thread {

    private Map<UUID, Socket> socketMap = new HashMap<>();

    public void setSocketMap(Map<UUID, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_MASTER_REDUCER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                try (
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ) {
                    Request request = (Request) input.readObject();
                    Socket clientSocket = socketMap.get(request.getId());
                    sendResponseToClient(clientSocket, request);
                } catch (IOException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void sendResponseToClient(Socket socket, Request request) {
        try (
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ) {
            output.writeObject(request);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
