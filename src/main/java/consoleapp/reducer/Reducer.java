package reducer;

import constant.Constants;
import master.MasterHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Reducer {

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private final int workerAmount;

    public static void main(String[] args) {
        int workerAmount = Integer.parseInt(args[0]);
        Reducer reducer = new Reducer(workerAmount);
        reducer.init();
    }

    public Reducer(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    private void init() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_REDUCER_PORT)) {
            connectToMaster();
            acceptRequest(serverSocket);
        } catch (IOException io) {
            System.out.println("Master init io error");
        }
    }

    private void connectToMaster() throws IOException {
        try (Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT)) {
            output = new ObjectOutputStream(requestSocket.getOutputStream());
            input = new ObjectInputStream(requestSocket.getInputStream());

            // SEND TO MASTER

        } catch (Exception exception) {
            System.out.println("Unexpected exception");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Exception exception) {
                System.out.println("Failure while closing streams");
            }
        }
    }

    private void acceptRequest(ServerSocket serverSocket) throws IOException {
        while (true) {
            // START REDUCER HANDLER THREAD
        }
    }
}
