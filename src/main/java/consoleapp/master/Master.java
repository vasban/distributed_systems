package master;

import constant.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Master {

    private final int workerAmount;
    private final List<Socket> workerConnections = new ArrayList<>();

    public static void main(String[] args) {
        int workerAmount = Integer.parseInt(args[0]);
        Master master = new Master(workerAmount);
        master.init();
    }

    public Master(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    private void init() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_MASTER_PORT)) {
            connectToWorkers();
            acceptRequest(serverSocket);
        } catch (IOException io) {
            System.out.println("Master init io error");
        }
    }

    private void connectToWorkers() throws IOException {
        for (int i = 0; i < this.workerAmount; i++) {
            int workerPort = Constants.DEFAULT_WORKER_PORT + i;
            Socket socket = new Socket(Constants.DEFAULT_WORKER_HOST, workerPort);
            workerConnections.add(socket);
            System.out.println("Connected to worker with port " + workerPort);
        }
    }

    private void acceptRequest(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            MasterHandler requestHandler = new MasterHandler();
            requestHandler.setSocket(socket);
            requestHandler.setWorkerConnections(workerConnections);
            requestHandler.start();
        }
    }
}
