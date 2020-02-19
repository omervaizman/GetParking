package com.example.getparking;

import android.graphics.Bitmap;

public class ParkingPost {
    //parking post is a class for all the parking in the database
    private String uid;
    private String location ;
    private String phone;
    private String name;
    private String price;
    private String fromDate;
    private String toDate;
    private String postId;

    public ParkingPost(){}

    public ParkingPost(String uid, String location,  String phone, String name, String price, String fromDate, String toDate) {
        this.uid = uid;
        this.location = location;
        this.phone = phone;
        this.name = name;
        this.price = price;
        this.fromDate = fromDate;
        this.toDate = toDate;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setPostId(String postId) { this.postId = postId; }
    public String getPostId(){return this.postId;}
}
