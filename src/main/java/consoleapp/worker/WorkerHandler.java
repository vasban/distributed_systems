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

public class WorkerHandler extends Thread {

    private Request request;
    private List<Accommodation> accommodations = new ArrayList<>();

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
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

    private void search(Request request) {
        System.out.println("I am searching");
    }

    //TODO
    private void registerDates(Request request) {
        System.out.println("I am registering dates");
    }

    //TODO
    private void viewReservations(Request request) {
        System.out.println("I am viewing Reservations;");
    }
}
