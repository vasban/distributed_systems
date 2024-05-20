package worker;

import constant.Constants;
import model.Accommodation;
import model.Request;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class WorkerHandler extends Thread {

    private final Request request;
    private final List<Accommodation> accommodations;

    public WorkerHandler(List<Accommodation> accommodations, Request request) {
        this.accommodations = accommodations;
        this.request = request;
    }

    @Override
    public void run() {
        if (request.getAction().equalsIgnoreCase("REGISTER_ACCOMMODATIONS")) {
            registerAccommodations(request);
        } else if (request.getAction().equalsIgnoreCase("REGISTER_DATES")) {
            registerDates(request);
        } else if (request.getAction().equalsIgnoreCase("VIEW_RESERVATIONS")) {
            viewReservations(request);
        } else if (request.getAction().equalsIgnoreCase("SEARCH")) {
            search(request);
        }
    }

    private void registerAccommodations(Request request) {
        accommodations.addAll(request.getAccommodations());
    }

    private void registerDates(Request request) {
        if (request.getAccommodations().size() != 1) return;
        Accommodation requestAccommodation = request.getAccommodations().get(0);

        Accommodation accommodation = accommodations.stream()
                .filter(item -> item.getId().equals(requestAccommodation.getId()))
                .findFirst().orElse(null);

        if (accommodation != null) {
            accommodation.getAvailableDates().addAll(requestAccommodation.getAvailableDates());
        }
    }

    private void viewReservations(Request request) {
        List<Accommodation> userAccommodations = accommodations.stream()
                .filter(item -> item.getUserId().equals(request.getUserId()))
                .toList();

        request.setAccommodations(userAccommodations);

        sendToReducer(request);
    }

    private void search(Request request) {
        // TODO: FILTER ACCOMMODATIONS

        sendToReducer(request);
    }

    private void sendToReducer(Request request) {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_REDUCER_HOST, Constants.DEFAULT_REDUCER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
        ) {
            output.writeObject(request);
            output.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
