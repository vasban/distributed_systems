package master;

import model.Accommodation;
import model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class MasterHandler extends Thread {

    private Socket socket;
    private List<Socket> workerConnections = new ArrayList<>();

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setWorkerConnections(List<Socket> workerConnections) {
        this.workerConnections = workerConnections;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream())
        ) {
            Request request = (Request) input.readObject();
            if (request.getAction() == null) return;

            if (request.getAction().equalsIgnoreCase("REGISTER_ACCOMMODATIONS")) {
                registerAccommodations(request);
            } else if (request.getAction().equalsIgnoreCase("REGISTER_DATES")) {
                transmit(request);
            } else if (request.getAction().equalsIgnoreCase("VIEW_RESERVATIONS")) {
                broadcast(request);
            } else if (request.getAction().equalsIgnoreCase("SEARCH")) {
                broadcast(request);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Unexpected exception while parsing request");
        }
    }

    private void registerAccommodations(Request request) {
        for (Accommodation accommodation: request.getAccommodations()) {
            accommodation.setId(UUID.randomUUID());
        }

        transmit(request);
    }

    private void transmit(Request request) {
        for (Accommodation accommodation: request.getAccommodations()) {
            Request nodeRequest = new Request(request.getAction());
            nodeRequest.setId(request.getId());
            nodeRequest.setUserId(request.getUserId());
            nodeRequest.setAccommodations(List.of(accommodation));

            int nodeId = hashCalculation(accommodation.getRoomName());
            Socket workerSocket = workerConnections.get(nodeId);
            try {
                ObjectOutputStream output = new ObjectOutputStream(workerSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(workerSocket.getInputStream());
                output.writeObject(request);
                output.flush();
                System.out.println("Sent transmit request with id " + request.getId() + " for room " + accommodation.getRoomName() + " to node " + nodeId);
            } catch (IOException io) {
                System.out.println("IO Exception while sending transmit request");
            }
        }
    }

    private void broadcast(Request request) {
        for (Socket workerSocket : workerConnections) {
            try {
                ObjectOutputStream output = new ObjectOutputStream(workerSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(workerSocket.getInputStream());
                output.writeObject(request);
                output.flush();
                System.out.println("Sent broadcast search request with id " + request.getId());
            } catch (IOException io) {
                System.out.println("IO Exception while sending broadcast request");
            }
        }
    }

    private int hashCalculation(String value) {
        int hashCode = value.hashCode();
        int size = workerConnections.size();
        return Math.abs(hashCode) % size;
    }
}
