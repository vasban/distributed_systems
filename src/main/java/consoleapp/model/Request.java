package model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Request implements Serializable {

    private UUID id;
    private String action;
    private Integer userId;
    private Integer review;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer noOfPersons;
    private Double stars;
    private DatePair availableDates;
    private String area;
    private List<Accommodation> accommodations;

    public Request() {
        this.id = UUID.randomUUID();
    }

    public Request(String action) {
        this.id = UUID.randomUUID();
        this.action = action;
    }

    public Integer getNoOfPersons() {
        return noOfPersons;
    }

    public void setNoOfPersons(Integer noOfPersons) {
        this.noOfPersons = noOfPersons;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public DatePair getAvailableDates() {
        return this.availableDates;
    }

    public void setAvailableDates(DatePair availableDates) {
        this.availableDates = availableDates;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getReview() {
        return review;
    }

    public void setReview(Integer review) {
        this.review = review;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", userId=" + userId +
                ", review=" + review +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", noOfPersons=" + noOfPersons +
                ", stars=" + stars +
                ", availableDates=" + availableDates +
                ", area='" + area + '\'' +
                ", accommodations=" + accommodations +
                '}';
    }

    public Request buildClone() {
        Request request = new Request();
        request.setId(this.id);
        request.setAction(this.action);
        request.setUserId(this.userId);
        request.setReview(this.review);
        request.setMinPrice(this.minPrice);
        request.setMaxPrice(this.maxPrice);
        request.setNoOfPersons(this.noOfPersons);
        request.setStars(this.stars);
        request.setArea(this.area);
        request.setAvailableDates(this.availableDates);
        request.setAccommodations(this.accommodations);
        return request;
    }
}
