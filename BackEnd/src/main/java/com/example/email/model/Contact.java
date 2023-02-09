package com.example.email.model;

import java.util.ArrayList;

public class Contact {
    private Integer contactCounter;
    private String name;
    private String phoneNumber;
    private ArrayList<String> emails;

    public Contact(Integer contactCounter, String name, String phoneNumber, ArrayList<String> emails) {
        this.contactCounter = contactCounter;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emails = emails;
    }
}
