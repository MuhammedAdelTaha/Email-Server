package com.example.email.controller;

import com.example.email.model.Email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmailsController implements Cloneable{
    private Map<Integer, Email> emails = new HashMap<>();

    public void add(Integer id, String sender, String receiver, String emailSubject, String emailBody, String date, int priority,
                    ArrayList<String> names){
        this.emails.put(id, new Email(id, sender, receiver, emailSubject, emailBody, date, priority, names));
    }

    public void remove(Integer id){
        this.emails.remove(id);
    }

    public void clear(){
        this.emails.clear();
    }

    public Email getById(Integer id){
        if(this.emails.containsKey(id)){
            return this.emails.get(id);
        }
        return null;
    }

    public boolean isContain(Integer id){
        return this.emails.containsKey(id);
    }

    public Map<Integer, Email> getEmails() {
        return emails;
    }

    public void setEmails(Map<Integer, Email> emails) {
        this.emails = emails;
    }
}
