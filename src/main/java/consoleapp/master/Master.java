package master;

import constant.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {

    private final int workerAmount;
    private final List<Socket> workerConnections = new ArrayList<>();
    private final Map<UUID, Socket> socketMap = new HashMap<>();

    public Master(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    public void init() {
        connectToWorkers();
        initiateReducerListener();
        acceptRequest();
    }

    private void connectToWorkers() {
        for (int i = 0; i < this.workerAmount; i++) {
            int workerPort = Constants.DEFAULT_WORKER_PORT + i;
            try {
                Socket socket = new Socket(Constants.DEFAULT_WORKER_HOST, workerPort);
                workerConnections.add(socket);
                System.out.println("Connected to worker with port " + workerPort);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void initiateReducerListener() {
        MasterReducerListener masterReducerListener = new MasterReducerListener();
        masterReducerListener.setSocketMap(socketMap);
        masterReducerListener.start();
    }

    private void acceptRequest() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_MASTER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                MasterHandler requestHandler = new MasterHandler();
                requestHandler.setSocket(socket);
                requestHandler.setWorkerConnections(workerConnections);
                requestHandler.setSocketMap(socketMap);
                requestHandler.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
