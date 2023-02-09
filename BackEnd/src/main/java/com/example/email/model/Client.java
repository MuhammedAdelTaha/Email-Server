package com.example.email.model;

public class Client {
    private String userName;
    private String password;

    public Client(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }
    public String getPassword() {
        return this.password;
    }

}
