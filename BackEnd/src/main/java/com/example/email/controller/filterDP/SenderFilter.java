package com.example.email.controller.filterDP;

import com.example.email.model.Email;

import java.util.Map;
import java.util.stream.Collectors;

public class SenderFilter implements Criteria {
    private String sender;

    public SenderFilter(String sender) {
        this.sender = sender;
    }

    @Override
    public Map<Integer, Email> meetsCriteria(Map<Integer, Email> emails) {
        if(this.sender == null){
            return emails;
        }
        return emails.entrySet().stream().filter(map -> this.sender.equals(map.getValue().getSender()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
