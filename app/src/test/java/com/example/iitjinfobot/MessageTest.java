package com.example.iitjinfobot;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class MessageTest {

    @Test
    public void emptyMessage(){
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "11:05 PM", "", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void emptyId(){
        Boolean result = Message.isValidMessage("", "May 08 2021", "11:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidDate1(){
        Boolean result = Message.isValidMessage("abcd123", "Mayy 08 2021", "11:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidDate2(){
        Boolean result = Message.isValidMessage("abcd123", "May 32 2021", "11:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidDate3(){
        Boolean result = Message.isValidMessage("abcd123", "Auggust 06 2021", "11:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidDate4(){
        Boolean result = Message.isValidMessage("abcd123", "08 06 2021", "11:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidTime1(){
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "13:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidTime2(){
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "7:05 PM", "Hello!", "sent", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void invalidType(){
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "12:05 PM", "Hello!", "got", false, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void optionsConflict1(){
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "12:05 PM", "Hello!", "received", true, Collections.emptyList());
        assertThat(result).isFalse();
    }

    @Test
    public void optionsConflict2(){
        List<String> options = new ArrayList<>();
        options.add("Mess menu");
        options.add("Bus schedule");
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "12:05 PM", "Hello!", "received", false, options);
        assertThat(result).isFalse();
    }

    @Test
    public void optionsConflict3(){
        List<String> options = new ArrayList<>();
        options.add("Mess menu");
        options.add("Bus schedule");
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "12:05 PM", "Hello!", "sent", true, options);
        assertThat(result).isFalse();
    }

    @Test
    public void validInput1(){
        List<String> options = new ArrayList<>();
        options.add("Mess menu");
        options.add("Bus schedule");
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "12:05 PM", "Hello!", "received", true, options);
        assertThat(result).isTrue();
    }

    @Test
    public void validInput2(){
        List<String> options = new ArrayList<>();
        Boolean result = Message.isValidMessage("abcd123", "May 08 2021", "08:55 AM", "Hello!", "received", false, options);
        assertThat(result).isTrue();
    }

    @Test
    public void validInput3(){
        List<String> options = new ArrayList<>();
        options.add("Monday");
        options.add("Tuesday");
        options.add("Wednesday");
        Boolean result = Message.isValidMessage("abcd123", "Jun 15 2021", "08:55 AM", "Hello!", "received", true, options);
        assertThat(result).isTrue();
    }

}