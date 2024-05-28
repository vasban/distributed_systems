package reducer;

import model.Accommodation;
import model.Request;
import util.Constants;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReducerHandler extends Thread {

    private final UUID requestId;

    private int counter = 0;

    private int threshold = 0;

    private final List<Accommodation> accommodations = Collections.synchronizedList(new ArrayList<>());

    public ReducerHandler(UUID requestId) {
        this.requestId = requestId;
    }

    @Override
    public void run() {
        try (
                Socket requestSocket = new Socket(Constants.DEFAULT_MASTER_REDUCER_HOST, Constants.DEFAULT_MASTER_REDUCER_PORT);
                ObjectOutputStream output = new ObjectOutputStream(requestSocket.getOutputStream());
        ) {
            Request request = new Request();
            request.setId(requestId);
            request.setAccommodations(accommodations);
            System.out.println("Sending reduced results for request with ID " + requestId);
            output.writeObject(request);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addResults(List<Accommodation> accommodations) {
        this.accommodations.addAll(accommodations);
        counter++;

        System.out.println("Received " + accommodations.size() + " results from worker " + counter + " for request with ID " + requestId);

        if (counter == threshold) {
            start();
        }
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
