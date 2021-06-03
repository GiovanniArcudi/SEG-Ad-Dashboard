package model.models;

import java.time.LocalDateTime;

public class Cost {
    LocalDateTime date;
    Double cost;

    public LocalDateTime getDate() {
        return date;
    }

    public Double getCost() {
        return cost;
    }

    public Cost(LocalDateTime date, Double cost) {
        this.date = date;
        this.cost = cost;
    }
}
