package com.example.fproject1;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String userName;
    private String text;

    public Review(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    public Review() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}