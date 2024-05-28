package master;

import util.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Listens to requests from managers and clients
public class MasterListener extends Thread {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_MASTER_PORT)) {
            System.out.println("Master listening for requests on port " + Constants.DEFAULT_MASTER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                MasterHandler requestHandler = new MasterHandler(socket);
                requestHandler.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
