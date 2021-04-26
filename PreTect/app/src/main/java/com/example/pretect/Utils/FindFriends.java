package com.example.pretect.Utils;

public class FindFriends {

    public String picture, name, email, key;

    public FindFriends() {
    }

    public FindFriends(String picture, String name, String email) {
        this.picture = picture;
        this.name = name;
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
