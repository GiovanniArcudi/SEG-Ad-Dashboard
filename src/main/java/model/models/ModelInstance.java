package model.models;

import model.utilities.DateService;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ModelInstance {
    private String date;
    private final String uuid;
    private final String id;

    ModelInstance(String date, String id) {

        this.date = date;
        this.id = id;
        this.uuid = UUID.nameUUIDFromBytes((id + date).getBytes()).toString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return DateService.parseDate(date);
    }

    public void setDate(LocalDateTime date) {
        this.date = DateService.toString(date);
    }
}
