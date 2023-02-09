package com.example.email.controller;

import com.example.email.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactController {
    private Map<Integer, Contact> contacts = new HashMap<>();

    public void add(Integer contactCounter , String name , String phoneNumber, ArrayList<String> emails){
        this.contacts.put(contactCounter , new Contact(contactCounter, name, phoneNumber, emails));
    }

    public void remove(Integer id){
        this.contacts.remove(id);
    }

    public boolean contains(Integer id){
        return this.contacts.containsKey(id);
    }

    public Map<Integer, Contact> getContacts() {
        return contacts;
    }
}
