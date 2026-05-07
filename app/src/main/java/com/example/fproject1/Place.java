package com.example.fproject1;

import java.io.Serializable;

public class Place implements Serializable {

    private String name;
    private String city;
    private String description;
    private String hours;
    private String fee;
    private String address;
    private int image;

    public Place(String name, String city, String description,
                 String hours, String fee, String address, int image) {

        this.name = name;
        this.city = city;
        this.description = description;
        this.hours = hours;
        this.fee = fee;
        this.address = address;
        this.image = image;
    }

    public String getName() {
        return name; }
    public String getCity() {
        return city; }
    public String getDescription() {
        return description; }
    public String getHours() {
        return hours; }
    public String getFee() {
        return fee; }
    public String getAddress() {
        return address; }
    public int getImage() {
        return image; }

    public void setName(String name) {
        this.name = name; }

}