package com.example.students.data;

import java.util.Date;

public class Comment_Chat {
    private String user;//name of commenter
    private String text;//text of comment
    private String imgId;//profile image
    private String email;//email of commenter
    private String id; // id comment (Key)
    private int hour;
    private int min;
    private int day;
    private int month;
    private Date d;
    private String type;



    public Comment_Chat(){}

    public Comment_Chat(String user, String text, String imgId){
        this.user=user;
        this.text=text;
        this.imgId=imgId;

    }

    public Date getD() {
        return d;
    }

    public void setD(Date d) {
        this.d = d;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
