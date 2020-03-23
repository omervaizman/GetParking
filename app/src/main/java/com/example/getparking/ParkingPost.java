package com.example.getparking;


public class ParkingPost {
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


    public String getPhone() {
        return phone;
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


    public String getFromDate() {
        return fromDate;
    }


    public String getToDate() {
        return toDate;
    }


    public String getUid() {
        return uid;
    }

    public void setPostId(String postId) { this.postId = postId; }
    public String getPostId(){return this.postId;}
}
