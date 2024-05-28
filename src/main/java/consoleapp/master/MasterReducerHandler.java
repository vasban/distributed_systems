package master;

import model.ConnectionDetails;
import model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Handles responses from reducer and sends them to the front end
public class MasterReducerHandler extends Thread {

    private final Socket socket;

    public MasterReducerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        ) {
            Request reducerResponse = (Request) input.readObject();
            System.out.println("Received response from reducer for request with ID: " + reducerResponse.getId());
            ConnectionDetails connectionDetails = Master.connectionDetailsMap.get(reducerResponse.getId());
            sendResponseToClient(connectionDetails, reducerResponse);
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void sendResponseToClient(ConnectionDetails connectionDetails, Request reducerResponse) {
        connectionDetails.sendResponse(reducerResponse);
        connectionDetails.closeStreams();
    }
}
