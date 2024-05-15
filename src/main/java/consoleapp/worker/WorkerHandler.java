package worker;

import model.Accommodation;
import model.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    }

    private void search(Request request) {
        System.out.println("I am searching");
    }
}
