package util;

import model.Accommodation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static JSONObject parseFileToObject(String jsonFileName) {
        try {
            Path path = Paths.get(jsonFileName);
            byte[] byteArray = Files.readAllBytes(path);
            return new JSONObject(new String(byteArray));
        } catch (IOException e) {
            System.out.println("JsonObject error!");
            return null;
        }
    }

    public static JSONArray parseFileToArray(String jsonFileName) {
        try {
            Path path = Paths.get(jsonFileName);
            byte[] byteArray = Files.readAllBytes(path);
            return new JSONArray(new String(byteArray));
        } catch (IOException e) {
            System.out.println("JsonObject error!");
            return null;
        }
    }

    public static List<Accommodation> convertToAccommodations(JSONArray jsonArray) {
        List<Accommodation> accommodationList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            Accommodation accommodation = new Accommodation();
            accommodation.setUserId(jsonObject.getInt("userId"));
            accommodation.setRoomName(jsonObject.getString("roomName"));
            accommodation.setNoOfPersons(jsonObject.getInt("noOfPersons"));
            accommodation.setArea(jsonObject.getString("area"));
            accommodation.setStars(jsonObject.getInt("stars"));
            accommodation.setNoOfReviews(jsonObject.getInt("noOfReviews"));
            accommodation.setRoomImage(jsonObject.getString("roomImage"));
            accommodationList.add(accommodation);
        }
        return accommodationList;
    }
}
