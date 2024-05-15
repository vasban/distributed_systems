package manager;

import model.Accommodation;
import model.DatePair;
import model.Request;
import org.json.JSONArray;
import util.JsonParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

public class ManagerAppService {

    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    public ManagerAppService(ObjectInputStream input, ObjectOutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void addAccommodation(String jsonPath) throws IOException {
        JSONArray jsonArray = JsonParser.parseFileToArray(jsonPath);
        List<Accommodation> accommodations = JsonParser.convertToAccommodations(jsonArray);
        Request request = new Request("REGISTER_ACCOMMODATIONS");
        request.setAccommodations(accommodations);
        output.writeObject(request);
        output.flush();
    }

    public void addAvailableDates(UUID accommodationId, DatePair datePair) throws IOException {
        Request request = new Request("REGISTER_DATES");
        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.getAvailableDates().add(datePair);
        request.setAccommodations(List.of(accommodation));
        output.writeObject(request);
        output.flush();
    }

    public List<Accommodation> viewAccommodations(Integer userId) throws IOException, ClassNotFoundException {
        Request request = new Request("VIEW_ACCOMMODATIONS");
        request.setUserId(userId);
        output.writeObject(request);
        output.flush();
        List<Accommodation> accommodations = (List<Accommodation>) input.readObject();
        return accommodations;
    }
}
