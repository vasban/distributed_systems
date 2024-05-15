package manager;

import model.Accommodation;
import model.DatePair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ManagerApp {

    private static final String DEFAULT_MASTER_HOST = "localhost";
    private static final int DEFAULT_MASTER_PORT = 50000;

    private ManagerAppService managerAppService;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private Scanner scanner;

    public static void main(String[] args) {
        ManagerApp managerApp = new ManagerApp();
        managerApp.init();
    }

    private void init() {
        try (Socket requestSocket = new Socket(DEFAULT_MASTER_HOST, DEFAULT_MASTER_PORT)) {
            output = new ObjectOutputStream(requestSocket.getOutputStream());
            input = new ObjectInputStream(requestSocket.getInputStream());
            managerAppService = new ManagerAppService(input, output);
            mainMenu();
        } catch (Exception exception) {
            System.out.println("Unexpected exception");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (Exception exception) {
                System.out.println("Failure while closing streams");
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
        managerAppService.addAccommodation(jsonPath);
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

                    managerAppService.addAvailableDates(id, datePair);
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
            List<Accommodation> accommodations = managerAppService.viewAccommodations(userId);
            for(Accommodation accommodation: accommodations) {
                System.out.println("----------------------------");
                System.out.println("Accommodation ID: " + accommodation.getId());
                System.out.println("Room Name: " + accommodation.getRoomName());
                System.out.println("Area: " + accommodation.getArea());
                System.out.println("----------------------------");
            }
        } catch (ClassNotFoundException exception) {
            System.out.println("Unexpected exception - cant find class uwu (probably a minority class)");
        }
        mainMenu();
    }

    private void getReservations() throws IOException {
        System.out.println("Insert user id: ");
        Integer userId = Integer.parseInt(scanner.nextLine());
        try {
            List<Accommodation> accommodations = managerAppService.viewAccommodations(userId);
            for(Accommodation accommodation: accommodations) {
                System.out.println("----------------------------");
                System.out.println("Accommodation ID: " + accommodation.getId());
                for (DatePair datePair: accommodation.getReservationDates()) {
                    System.out.println("============================");
                    System.out.println("Start date: " + datePair.getStartDate());
                    System.out.println("End date: " + datePair.getEndDate());
                    System.out.println("============================");
                }
                System.out.println("----------------------------");
            }
        } catch (ClassNotFoundException exception) {
            System.out.println("Unexpected exception - cant find class uwu (probably a minority class)");
        }
        mainMenu();
    }
}
