package io.github.shanerwu.message.format.parser.message;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class MessageData {

    private SimpleStringProperty message;

    private SimpleStringProperty description;

    private List<MessageData> details;

    public MessageData(String description, String message) {
        this.description = new SimpleStringProperty(description);
        this.message = new SimpleStringProperty(message);
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public List<MessageData> getDetails() {
        return details;
    }

    public void setDetails(List<MessageData> details) {
        this.details = details;
    }

}
