package reducer;

import constant.Constants;
import model.Accommodation;
import model.Request;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReducerHandler extends Thread {

    private final UUID requestId;

    private int counter = 0;

    private int threshold = 0;

    private final List<Accommodation> accommodations = new ArrayList<>();

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
            System.out.println("I do be reducing and writting requests and shiiii we waz kangz in AFRRRICAAAA!X.X.X.X. "+request.getId());
            output.writeObject(request);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addResults(List<Accommodation> accommodations) {
        this.accommodations.addAll(accommodations);
        counter++;

        if (counter == threshold) {
            start();
        }
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
