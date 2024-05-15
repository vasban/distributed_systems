package model;

import java.io.Serializable;
import java.time.LocalDate;

public class DatePair implements Serializable {

    private LocalDate startDate;
    private LocalDate endDate;

    public DatePair(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "DatePair{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
