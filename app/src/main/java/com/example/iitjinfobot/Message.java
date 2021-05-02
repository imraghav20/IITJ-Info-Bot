package com.example.iitjinfobot;

import java.util.List;

public class Message {
    String id, date, time, message, type;
    Boolean hasOptions;
    List<String> options;

    public Message() {
    }

    public Message(String id, String date, String time, String message, String type, Boolean hasOptions, List<String> options) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.message = message;
        this.type = type;
        this.hasOptions = hasOptions;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getHasOptions() {
        return hasOptions;
    }

    public void setHasOptions(Boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
