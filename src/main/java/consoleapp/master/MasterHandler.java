package master;

import model.Accommodation;
import model.ConnectionDetails;
import model.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

// Handles requests from managers and clients and sends them to the workers
public class MasterHandler extends Thread {

    private final Socket socket;

    public MasterHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ConnectionDetails connectionDetails = new ConnectionDetails(this.socket);
        connectionDetails.initStreams();

        Request request = connectionDetails.receiveRequest();
        if (request == null || request.getAction() == null) return;

        System.out.println("Handling request with ID: " + request.getId());
        Master.connectionDetailsMap.put(request.getId(), connectionDetails);

        if (request.getAction().equalsIgnoreCase("REGISTER_ACCOMMODATIONS")) {
            registerAccommodations(request);
        } else if (request.getAction().equalsIgnoreCase("REGISTER_DATES")) {
            registerDates(request);
        } else if (request.getAction().equalsIgnoreCase("VIEW_ACCOMMODATIONS")) {
            broadcast(request);
        } else if (request.getAction().equalsIgnoreCase("VIEW_RESERVATIONS")) {
            broadcast(request);
        } else if (request.getAction().equalsIgnoreCase("SEARCH")) {
            broadcast(request);
        } else if (request.getAction().equalsIgnoreCase("CREATE_RESERVATION")) {
            transmit(request);
        } else if (request.getAction().equalsIgnoreCase("REVIEW")) {
            transmit(request);
        }
    }

    private void registerAccommodations(Request request) {
        transmit(request);

        ConnectionDetails connectionDetails = Master.connectionDetailsMap.get(request.getId());
        connectionDetails.closeStreams();

        Master.connectionDetailsMap.remove(request.getId());
    }

    private void registerDates(Request request) {
        transmit(request);

        ConnectionDetails connectionDetails = Master.connectionDetailsMap.get(request.getId());
        connectionDetails.closeStreams();

        Master.connectionDetailsMap.remove(request.getId());
    }

    private void transmit(Request request) {
        for (Accommodation accommodation : request.getAccommodations()) {
            Request nodeRequest = request.buildClone();
            nodeRequest.setAccommodations(List.of(accommodation));

            int nodeId = hashCalculation(accommodation.getRoomName());
            Socket workerSocket = Master.workerConnections.get(nodeId);
            try {
                ObjectOutputStream output = new ObjectOutputStream(workerSocket.getOutputStream());
                output.writeObject(nodeRequest);
                output.flush();
                System.out.println("Sent transmit request with id " + request.getId() + " for room " + accommodation.getRoomName() + " to node " + nodeId);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void broadcast(Request request) {
        for (Socket workerSocket : Master.workerConnections) {
            try {
                ObjectOutputStream output = new ObjectOutputStream(workerSocket.getOutputStream());
                output.writeObject(request);
                output.flush();
                System.out.println("Sent broadcast search request with id " + request.getId());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private int hashCalculation(String value) {
        int hashCode = value.hashCode();
        int size = Master.workerConnections.size();
        return Math.abs(hashCode) % size;
    }
}
