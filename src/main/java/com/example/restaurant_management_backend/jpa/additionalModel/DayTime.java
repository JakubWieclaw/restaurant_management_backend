package com.example.restaurant_management_backend.jpa.additionalModel;

import jakarta.persistence.Embeddable;

import java.time.LocalTime;

@Embeddable
public class DayTime {
    private LocalTime openingTime;
    private LocalTime closingTime;

    public DayTime(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public DayTime() {

    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

}
