package com.example.getparking;


import java.io.Serializable;


@SuppressWarnings("serial")
public class User implements Serializable {
    public String uid ;
    public String firstName;
    public String lastName;
    public String email;

    public User() {
        // Default for firebase
    }

    public User(String uid ,  String firstName, String lastName, String email) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    public void setUser(User temp)
    {
        this.uid = temp.uid;
        this.firstName = temp.firstName;
        this.lastName = temp.lastName;
        this.email = temp.email;
    }



}

