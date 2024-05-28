package master;

import util.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Listens to responses from reducer
public class MasterReducerListener extends Thread {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_MASTER_REDUCER_PORT)) {
            System.out.println("Master listening for requests on port " + Constants.DEFAULT_MASTER_REDUCER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                MasterReducerHandler masterReducerHandler = new MasterReducerHandler(socket);
                masterReducerHandler.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
