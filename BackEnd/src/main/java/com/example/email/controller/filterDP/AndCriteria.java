package com.example.email.controller.filterDP;

import com.example.email.model.Email;

import java.util.Map;

public class AndCriteria implements Criteria {
    private Criteria[] criterias;

    public AndCriteria(Criteria... criterias) {
        this.criterias = criterias;
    }

    @Override
    public Map<Integer, Email> meetsCriteria(Map<Integer, Email> emails) {
        Map<Integer, Email> filteredEmails = emails;

        for (Criteria criteria : this.criterias) {
            filteredEmails = criteria.meetsCriteria(filteredEmails);
        }

        return filteredEmails;
    }
}
