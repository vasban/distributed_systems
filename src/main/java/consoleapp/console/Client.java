package console;

import model.Accommodation;
import model.DatePair;
import model.Request;
import util.Constants;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Client {

    private Scanner scanner;

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
    }

    private void init() {
        mainMenu();
    }

    private void mainMenu() {
        scanner = new Scanner(System.in);
        boolean isLoop = true;
        while (isLoop) {
            System.out.println("Choose from the following options");
            System.out.println("-------------------------\n");
            System.out.println("1 - Search");
            System.out.println("2 - Create reservation");
            System.out.println("3 - Review");
            System.out.println("4 - Quit");

            int selection = Integer.parseInt(scanner.nextLine());
            switch (selection) {
                case 1:
                    searchFilters();
                    break;
                case 2:
                    createReservation();
                    break;
                case 3:
                    review();
                    break;
                case 4:
                    System.out.println("Closing application.");
                    isLoop = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }

    private void searchFilters() {
        boolean isLoop = true;
        while (isLoop) {
            System.out.println("Choose which filter to apply");
            System.out.println("-------------------------------\n");
            System.out.println("1 - Available dates");
            System.out.println("2 - Number of people");
            System.out.println("3 - Number of stars");
            System.out.println("4 - Area");
            System.out.println("5 - Price");
            System.out.println("6 - Close");

            int selection = Integer.parseInt(scanner.nextLine());

            Request request = new Request("SEARCH");
            switch (selection) {
                case 1:
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    System.out.println("Insert start date (DD/MM/YYYY):\n");
                    String startDateString = scanner.nextLine();
                    LocalDate startDate = LocalDate.parse(startDateString, formatter);

                    System.out.println("Insert end date (DD/MM/YYYY):\n");
                    String endDateString = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(endDateString, formatter);

                    DatePair datePair = new DatePair(startDate, endDate);
                    request.setAvailableDates(datePair);
                    break;
                case 2:
                    System.out.println("Insert number of people:");
                    int noOfPeople = Integer.parseInt(scanner.nextLine());
                    request.setNoOfPersons(noOfPeople);
                    break;
                case 3:
                    System.out.println("Insert number of stars:");
                    double stars = Double.parseDouble(scanner.nextLine());
                    request.setStars(stars);
                    break;
                case 4:
                    System.out.println("Insert area name:");
                    String area = scanner.nextLine();
                    request.setArea(area);
                    break;
                case 5:
                    System.out.println("Insert min price");
                    int minPrice = Integer.parseInt(scanner.nextLine());
                    System.out.println("Insert max price");
                    int maxPrice = Integer.parseInt(scanner.nextLine());
                    request.setMinPrice(minPrice);
                    request.setMaxPrice(maxPrice);
                    break;
                case 6:
                    isLoop = false;
                    continue;
                default:
                    System.out.println("Invalid input. Please try again.");
                    continue;
            }

            try (
                    Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                    ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
            ) {
                output.writeObject(request);
                output.flush();
                Request answer = (Request) input.readObject();

                for (Accommodation accommodation : answer.getAccommodations()) {
                    System.out.println("----------------------------");
                    System.out.println("Accommodation Name: " + accommodation.getRoomName());
                    for (DatePair datePair : accommodation.getReservationDates()) {
                        System.out.println("============================");
                        System.out.println("Start date: " + datePair.getStartDate());
                        System.out.println("End date: " + datePair.getEndDate());
                        System.out.println("============================");
                    }
                    System.out.println("----------------------------");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        System.out.println();
    }

    private void createReservation() {
        boolean isLoop = true;
        while (isLoop) {
            System.out.println("Choose from the following options");
            System.out.println("-------------------------\n");
            System.out.println("1 - Enter a range of dates");
            System.out.println("2 - Return");
            int selection = Integer.parseInt(scanner.nextLine());

            switch (selection) {
                case 1:
                    System.out.println("Insert accommodation name:\n");
                    String roomName = scanner.nextLine();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    System.out.println("Insert start date (DD/MM/YYYY):\n");
                    String startDateString = scanner.nextLine();
                    LocalDate startDate = LocalDate.parse(startDateString, formatter);

                    System.out.println("Insert end date (DD/MM/YYYY):\n");
                    String endDateString = scanner.nextLine();
                    LocalDate endDate = LocalDate.parse(endDateString, formatter);

                    DatePair datePair = new DatePair(startDate, endDate);

                    addReservationDates(roomName, datePair);
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
    }

    public void addReservationDates(String roomName, DatePair datePair) {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            Request request = new Request("CREATE_RESERVATION");
            Accommodation accommodation = new Accommodation();
            accommodation.setRoomName(roomName);
            accommodation.getReservationDates().add(datePair);
            request.setAccommodations(List.of(accommodation));
            output.writeObject(request);
            output.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void review() {
        System.out.println("Insert accommodation name:\n");
        String roomName = scanner.nextLine();

        System.out.println("Insert review (1-5):\n");
        Integer review = Integer.parseInt(scanner.nextLine());

        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_HOST, Constants.DEFAULT_MASTER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(requestSocket.getInputStream());
        ) {
            Request request = new Request("REVIEW");
            request.setReview(review);
            Accommodation accommodation = new Accommodation();
            accommodation.setRoomName(roomName);
            request.setAccommodations(List.of(accommodation));
            output.writeObject(request);
            output.flush();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println();
    }
}
