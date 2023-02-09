package com.example.email.controller;

import com.example.email.model.Email;
import com.example.email.model.Folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FoldersController {
    private Map<Integer, Folder> folders = new HashMap<>();

    public void add(String name, ArrayList<Integer> ids, Integer folderCounter, String folderName, EmailsController emails){
        if(!folderName.equals("contacts")){
            Map<Integer, Email> tempEmails = new HashMap<>();
            for(Integer id : ids){
                tempEmails.put(id, emails.getById(id));
            }
            this.folders.put(folderCounter, new Folder(folderCounter, name, tempEmails));
        }
    }

    public void remove(Integer id){
        this.folders.remove(id);
    }

    public boolean contains(Integer id){
        return this.folders.containsKey(id);
    }

    public Map<Integer, Folder> getFolders() {
        return folders;
    }

    public Folder getById(Integer id){
        if(this.folders.containsKey(id)){
            return this.folders.get(id);
        }
        return null;
    }
}
