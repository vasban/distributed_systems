package master;

import model.ConnectionDetails;
import util.Constants;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

// Initiates connections to workers, a listener to responses from reducer and a listener to requests from the front end
public class Master {

    private final int workerAmount;

    protected static final List<Socket> workerConnections = Collections.synchronizedList(new ArrayList<>());
    protected static final Map<UUID, ConnectionDetails> connectionDetailsMap = Collections.synchronizedMap(new HashMap<>());

    public Master(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    public void init() {
        connectToWorkers();
        initiateListeners();
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

    private void initiateListeners() {
        MasterReducerListener masterReducerListener = new MasterReducerListener();
        masterReducerListener.start();

        MasterListener masterListener = new MasterListener();
        masterListener.start();
    }
}
