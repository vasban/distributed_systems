package worker;

import model.Accommodation;
import model.DatePair;
import model.Request;
import util.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
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
        } else if (request.getAction().equalsIgnoreCase("VIEW_ACCOMMODATIONS")) {
            viewAccommodations(request);
        } else if (request.getAction().equalsIgnoreCase("VIEW_RESERVATIONS")) {
            viewAccommodations(request);
        } else if (request.getAction().equalsIgnoreCase("SEARCH")) {
            search(request);
        } else if (request.getAction().equalsIgnoreCase("CREATE_RESERVATION")) {
            createReservation(request);
        } else if (request.getAction().equalsIgnoreCase("REVIEW")) {
            review(request);
        }
    }

    private void registerAccommodations(Request request) {
        accommodations.addAll(request.getAccommodations());
    }

    private void registerDates(Request request) {
        if (request.getAccommodations().size() != 1) return;
        Accommodation requestAccommodation = request.getAccommodations().get(0);

        Accommodation accommodation = accommodations.stream()
                .filter(item -> item.getRoomName().equals(requestAccommodation.getRoomName()))
                .findFirst().orElse(null);

        if (accommodation != null) {
            System.out.println("Registering available dates for accommodation with room name: " + accommodation.getRoomName());
            accommodation.getAvailableDates().addAll(requestAccommodation.getAvailableDates());
        }
    }

    private void viewAccommodations(Request request) {
        List<Accommodation> userAccommodations = accommodations.stream()
                .filter(item -> item.getUserId().equals(request.getUserId()))
                .toList();

        request.setAccommodations(userAccommodations);

        sendToReducer(request);
    }

    private void search(Request request) {
        List<Accommodation> result = accommodations.stream().filter(accommodation -> {
            if (request.getArea() != null && !accommodation.getArea().equals(request.getArea())) {
                return false;
            }

            if (request.getNoOfPersons() != null && !accommodation.getNoOfPersons().equals(request.getNoOfPersons())) {
                return false;
            }

            if (request.getMinPrice() != null && request.getMaxPrice() != null && !(accommodation.getPrice() >= request.getMinPrice() && accommodation.getPrice() <= request.getMaxPrice())) {
                return false;
            }

            if (request.getStars() != null && !(accommodation.getStars().equals(request.getStars()))) {
                return false;
            }

            if (request.getAvailableDates() != null) {
                LocalDate startDate = request.getAvailableDates().getStartDate();
                LocalDate endDate = request.getAvailableDates().getEndDate();
                return accommodation.getAvailableDates().stream()
                        .anyMatch(datePair -> (startDate.isAfter(datePair.getStartDate()) || startDate.isEqual(datePair.getStartDate()))
                                && (endDate.isBefore(datePair.getEndDate()) || endDate.isEqual(datePair.getEndDate())));
            }

            return true;
        }).toList();

        request.setAccommodations(result);
        sendToReducer(request);
    }

    private void createReservation(Request request) {
        if (request.getAccommodations().size() != 1) return;
        Accommodation requestAccommodation = request.getAccommodations().get(0);

        Accommodation accommodation = accommodations.stream()
                .filter(item -> item.getRoomName().equals(requestAccommodation.getRoomName()))
                .findFirst().orElse(null);

        if (accommodation != null) {
            synchronized (accommodation) {
                DatePair reservationDatePair = requestAccommodation.getReservationDates().get(0);
                LocalDate startDate = reservationDatePair.getStartDate();
                LocalDate endDate = reservationDatePair.getEndDate();

                DatePair availableDatePair = accommodation.getAvailableDates().stream()
                        .filter(datePair -> (startDate.isAfter(datePair.getStartDate()) || startDate.isEqual(datePair.getStartDate()))
                                && (endDate.isBefore(datePair.getEndDate()) || endDate.isEqual(datePair.getEndDate())))
                        .findFirst().orElse(null);

                if (availableDatePair != null) {
                    System.out.println("Registering reservation dates for accommodation with room name: " + accommodation.getRoomName());
                    accommodation.getReservationDates().add(reservationDatePair);
                    breakAvailableDatePair(accommodation, availableDatePair, reservationDatePair);
                } else {
                    System.out.println("Failed to reserve dates for accommodation with room name: " + accommodation.getRoomName());
                }
            }
        }
    }

    private void breakAvailableDatePair(Accommodation accommodation, DatePair availableDatePair, DatePair reservationDatePair) {
        if (availableDatePair.getStartDate().isEqual(reservationDatePair.getStartDate()) && availableDatePair.getEndDate().isEqual(reservationDatePair.getEndDate())) {
            accommodation.getAvailableDates().remove(availableDatePair);
            System.out.println("Removed available date starting on " + availableDatePair.getStartDate() + " and ending on " + availableDatePair.getEndDate());
        } else if (availableDatePair.getStartDate().isEqual(reservationDatePair.getStartDate())) {
            availableDatePair.setStartDate(reservationDatePair.getEndDate().plusDays(1));
            System.out.println("Changed available date to start on " + availableDatePair.getStartDate() + " and end on " + availableDatePair.getEndDate());
        } else if (availableDatePair.getEndDate().isEqual(reservationDatePair.getEndDate())) {
            availableDatePair.setEndDate(reservationDatePair.getStartDate().minusDays(1));
            System.out.println("Changed available date to start on " + availableDatePair.getStartDate() + " and end on " + availableDatePair.getEndDate());
        } else {
            accommodation.getAvailableDates().remove(availableDatePair);

            DatePair leftDatePair = new DatePair(availableDatePair.getStartDate(), reservationDatePair.getStartDate().minusDays(1));
            DatePair rightDatePair = new DatePair(reservationDatePair.getEndDate().plusDays(1), availableDatePair.getEndDate());

            accommodation.getAvailableDates().add(leftDatePair);
            accommodation.getAvailableDates().add(rightDatePair);
            System.out.println("Created available date that starts on " + leftDatePair.getStartDate() + " and ends on " + leftDatePair.getEndDate());
            System.out.println("Created available date that starts on " + rightDatePair.getStartDate() + " and ends on " + rightDatePair.getEndDate());
        }
    }

    private void review(Request request) {
        if (request.getAccommodations().size() != 1) return;
        Accommodation requestAccommodation = request.getAccommodations().get(0);

        Accommodation accommodation = accommodations.stream()
                .filter(item -> item.getRoomName().equals(requestAccommodation.getRoomName()))
                .findFirst().orElse(null);

        if (accommodation != null) {
            Double totalAmountOfStars = accommodation.getStars() * accommodation.getNoOfReviews();
            Double newValue = (totalAmountOfStars + request.getReview()) / (accommodation.getNoOfReviews() + 1);
            System.out.println("Changing stars for accommodation with name " + accommodation.getRoomName() + " from " + accommodation.getStars() + " to " + newValue);
            accommodation.setStars(newValue);
        }
    }

    private void sendToReducer(Request request) {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_REDUCER_HOST, Constants.DEFAULT_REDUCER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            output.writeObject(request);
            output.flush();
            System.out.println("Sent worker response to reducer for request with ID " + request.getId());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
