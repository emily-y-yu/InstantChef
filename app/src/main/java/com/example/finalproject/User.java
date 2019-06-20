package com.example.finalproject;

// class storing all user information
public class User {

    private String email;

    public User() {
        email = "";
    }

    public User(String e) {
        email = e;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
