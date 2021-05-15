package com.example.iitjinfobot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Boolean isValidMessage(String id, String date, String time, String message, String type, Boolean hasOptions, List<String> options){
        if(id.isEmpty() || ! isValidDate(date) || ! isValidTime(time) || message.isEmpty() || ! (type.equals("received") || (type.equals("sent")))){
            return false;
        }
        else if(hasOptions){
            if(type.equals("sent")){
                return false;
            }
            return !options.isEmpty();
        }
        else if(!hasOptions){
            return options.isEmpty();
        }
        return true;
    }

    public static Boolean isValidTime(String time){
        String regexPattern = "(1[012]|0[1-9]):" + "[0-5][0-9](\\s)" + "?(?i)(am|pm)";
        Pattern compiledPattern = Pattern.compile(regexPattern);
        if(time == null){
            return false;
        }
        Matcher m = compiledPattern.matcher(time);
        return m.matches();
    }

    public static boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
