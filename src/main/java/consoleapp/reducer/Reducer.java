package reducer;

import model.Request;
import util.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Reducer {

    private final int workerAmount;

    private final Map<UUID, ReducerHandler> requestMapper = new HashMap<>();

    public Reducer(int workerAmount) {
        this.workerAmount = workerAmount;
    }

    public void init() {
        try (ServerSocket serverSocket = new ServerSocket(Constants.DEFAULT_REDUCER_PORT)) {
            acceptRequest(serverSocket);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void acceptRequest(ServerSocket serverSocket) throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();

            try (
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ) {
                Request request = (Request) input.readObject();
                System.out.println("Reducer accepted request with ID " + request.getId());
                reduce(request);
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void reduce(Request request) {
        UUID requestId = request.getId();

        if (requestMapper.containsKey(requestId)) {
            // If it's not the first worker to send results then use the existing handler and add results to it
            ReducerHandler reducerHandler = requestMapper.get(requestId);
            reducerHandler.addResults(request.getAccommodations());
        } else {
            // If it's the first worker to send results then create a new handler and add it to the map
            ReducerHandler reducerHandler = new ReducerHandler(requestId);
            reducerHandler.setThreshold(workerAmount);
            reducerHandler.addResults(request.getAccommodations());
            requestMapper.put(requestId, reducerHandler);
        }
    }
}
