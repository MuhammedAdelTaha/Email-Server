package com.example.email.controller.filterDP;

import com.example.email.model.Email;

import java.util.Map;
import java.util.stream.Collectors;

public class SubjectFilter implements Criteria {
    private String subject;

    public SubjectFilter(String subject) {
        this.subject = subject;
    }

    @Override
    public Map<Integer, Email> meetsCriteria(Map<Integer, Email> emails) {
        if(this.subject == null){
            return emails;
        }
        return emails.entrySet().stream().filter(map -> this.subject.equals(map.getValue().getEmailSubject()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
