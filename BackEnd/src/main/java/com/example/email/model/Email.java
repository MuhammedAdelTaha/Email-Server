package com.example.email.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class Email {
    private Integer id;
    private String sender;
    private String receiver;
    private String emailSubject;
    private String emailBody;
    private String date;
    private int priority;
    private ArrayList<String> names;

    public Email(Integer id, String sender, String receiver, String emailSubject, String emailBody, String date, int priority,
                 ArrayList<String> names) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.date = date;
        this.priority = priority;
        this.names = names;
    }

    public Integer getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getDate() {
        return date;
    }

    public int getPriority() {
        return priority;
    }

    public ArrayList<String> getNames() {
        return names;
    }
}
