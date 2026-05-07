package com.example.fproject1;

import java.io.Serializable;

public class City implements Serializable {

    private String name;
    private int image;
    public City(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }
}