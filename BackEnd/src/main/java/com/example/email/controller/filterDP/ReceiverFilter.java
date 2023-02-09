package com.example.email.controller.filterDP;

import com.example.email.model.Email;

import java.util.Map;
import java.util.stream.Collectors;

public class ReceiverFilter implements Criteria {
    private String receiver;

    public ReceiverFilter(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public Map<Integer, Email> meetsCriteria(Map<Integer, Email> emails) {
        if(this.receiver == null){
            return emails;
        }
        return emails.entrySet().stream().filter(map -> this.receiver.equals(map.getValue().getReceiver()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
