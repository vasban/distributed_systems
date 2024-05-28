package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Accommodation implements Serializable {

    private Integer userId;
    private String roomName;
    private Integer noOfPersons;
    private String area;
    private Double stars;
    private Integer noOfReviews;
    private Integer price;
    private String roomImage;

    private final List<DatePair> availableDates = new ArrayList<>();
    private final List<DatePair> reservationDates = new ArrayList<>();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(Integer noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public Integer getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(Integer noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public List<DatePair> getAvailableDates() {
        return availableDates;
    }

    public List<DatePair> getReservationDates() {
        return reservationDates;
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "userId=" + userId +
                ", roomName='" + roomName + '\'' +
                ", noOfPersons=" + noOfPersons +
                ", area='" + area + '\'' +
                ", stars=" + stars +
                ", noOfReviews=" + noOfReviews +
                ", price=" + price +
                ", roomImage='" + roomImage + '\'' +
                ", availableDates=" + availableDates +
                ", reservationDates=" + reservationDates +
                '}';
    }
}
