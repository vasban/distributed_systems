package worker;

import model.Accommodation;
import model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Worker extends Thread {

    private final int port;
    private final List<Accommodation> accommodations = Collections.synchronizedList(new ArrayList<>());

    private Socket masterSocket;

    public Worker(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            this.masterSocket = serverSocket.accept();
            System.out.println("Master is connected to worker with port " + this.port);

            while (true) {
                acceptRequest();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void acceptRequest() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(this.masterSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(this.masterSocket.getInputStream());

            Request request = (Request) input.readObject();
            if (request.getAction() == null) return;

            System.out.println("Worker on port " + this.port + " received request with ID: " + request.getId());

            WorkerHandler workerHandler = new WorkerHandler(accommodations, request);
            workerHandler.start();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
