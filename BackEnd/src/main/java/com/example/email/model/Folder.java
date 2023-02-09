package com.example.email.model;

import java.util.Map;

public class Folder {
    private Integer folderCounter;
    private String name;
    private Map<Integer, Email> folderMails;

    public Folder(Integer folderCounter, String name, Map<Integer, Email> folderMails) {
        this.folderCounter = folderCounter;
        this.name = name;
        this.folderMails = folderMails;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Email> getFolderMails() {
        return folderMails;
    }
}
