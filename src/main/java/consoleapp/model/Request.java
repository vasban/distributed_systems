package model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Request implements Serializable {

    private UUID id;
    private String action;
    private Integer userId;
    private List<Accommodation> accommodations;

    public Request() {
        this.id = UUID.randomUUID();
    }

    public Request(String action) {
        this.id = UUID.randomUUID();
        this.action = action;
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
                ", userId='" + userId + '\'' +
                ", accommodations=" + accommodations +
                '}';
    }
}
