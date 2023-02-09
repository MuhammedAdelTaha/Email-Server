package com.example.email.controller.filterDP;

import com.example.email.model.Email;

import java.util.Map;

public interface Criteria {
    public Map<Integer, Email> meetsCriteria(Map<Integer, Email> emails);
}
