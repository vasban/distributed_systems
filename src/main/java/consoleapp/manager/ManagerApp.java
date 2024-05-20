package manager;

import constant.Constants;
import model.Accommodation;
import model.DatePair;
import model.Request;
import org.json.JSONArray;
import util.JsonParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ManagerApp {

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private Scanner scanner;

    public static void main(String[] args) {
        ManagerApp managerApp = new ManagerApp();
        managerApp.init();
    }

    private void init() {
        try (Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT)) {
            output = new ObjectOutputStream(requestSocket.getOutputStream());
            input = new ObjectInputStream(requestSocket.getInputStream());
            mainMenu();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void mainMenu() throws IOException {
        scanner = new Scanner(System.in);
        boolean isLoop = true;
        while (isLoop) {
            System.out.println("Choose from the following options");
            System.out.println("-------------------------\n");
            System.out.println("1 - Register new accommodation");
            System.out.println("2 - Register available dates");
            System.out.println("3 - View accommodations");
            System.out.println("4 - View registered reservations for your accommodations");
            System.out.println("5 - Quit");

            int selection = Integer.parseInt(scanner.nextLine());
            switch (selection) {
                case 1:
                    addAccommodation();
                    break;
                case 2:
                    addAvailableDates();
                    break;
                case 3:
                    viewAccommodations();
                    break;
                case 4:
                    getReservations();
                    break;
                case 5:
                    System.out.println("Closing application.");
                    isLoop = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }

    private void addAccommodation() throws IOException {
        System.out.println("Insert path to json file: ");
        String jsonPath = scanner.nextLine();
        addAccommodation(jsonPath);
        System.out.println();
        mainMenu();
    }

    private void addAvailableDates() throws IOException {
        boolean isLoop = true;
        while (isLoop) {
            System.out.println("Choose from the following options");
            System.out.println("-------------------------\n");
            System.out.println("1 - Enter a range of dates");
            System.out.println("2 - Return");
            int selection = Integer.parseInt(scanner.nextLine());

            switch (selection) {
                case 1:
                    System.out.println("Insert accommodation id:\n");
                    String accommodationId = scanner.nextLine();
                    UUID id = UUID.fromString(accommodationId);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    System.out.println("Insert start date (DD-MM-YYYY):\n");
                    String startDateString = scanner.nextLine();
                    LocalDate startDate = LocalDate.parse(startDateString, formatter);

                    System.out.println("Insert end date (DD-MM-YYYY):\n");
                    String endDateString = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(endDateString, formatter);

                    DatePair datePair = new DatePair(startDate, endDate);

                    addAvailableDates(id, datePair);
                    break;
                case 2:
                    System.out.println("Go back.");
                    isLoop = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
        System.out.println();
        mainMenu();
    }

    private void viewAccommodations() throws IOException {
        System.out.println("Insert user id: ");
        Integer userId = Integer.parseInt(scanner.nextLine());
        try {
            List<Accommodation> accommodations = viewAccommodations(userId);
            for (Accommodation accommodation : accommodations) {
                System.out.println("----------------------------");
                System.out.println("Accommodation ID: " + accommodation.getId());
                System.out.println("Room Name: " + accommodation.getRoomName());
                System.out.println("Area: " + accommodation.getArea());
                System.out.println("----------------------------");
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        mainMenu();
    }

    private void getReservations() throws IOException {
        System.out.println("Insert user id: ");
        Integer userId = Integer.parseInt(scanner.nextLine());
        try {
            List<Accommodation> accommodations = viewAccommodations(userId);
            for (Accommodation accommodation : accommodations) {
                System.out.println("----------------------------");
                System.out.println("Accommodation ID: " + accommodation.getId());
                for (DatePair datePair : accommodation.getReservationDates()) {
                    System.out.println("============================");
                    System.out.println("Start date: " + datePair.getStartDate());
                    System.out.println("End date: " + datePair.getEndDate());
                    System.out.println("============================");
                }
                System.out.println("----------------------------");
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        mainMenu();
    }

    public void addAccommodation(String jsonPath) {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            JSONArray jsonArray = JsonParser.parseFileToArray(jsonPath);
            List<Accommodation> accommodations = JsonParser.convertToAccommodations(jsonArray);
            Request request = new Request("REGISTER_ACCOMMODATIONS");
            request.setAccommodations(accommodations);
            output.writeObject(request);
            output.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addAvailableDates(UUID accommodationId, DatePair datePair) {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            Request request = new Request("REGISTER_DATES");
            Accommodation accommodation = new Accommodation();
            accommodation.setId(accommodationId);
            accommodation.getAvailableDates().add(datePair);
            request.setAccommodations(List.of(accommodation));
            output.writeObject(request);
            output.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public List<Accommodation> viewAccommodations(Integer userId) throws ClassNotFoundException {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            Request request = new Request("VIEW_ACCOMMODATIONS");
            request.setUserId(userId);
            output.writeObject(request);
            output.flush();
            Request result = (Request) input.readObject();
            return result.getAccommodations();
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }
}
