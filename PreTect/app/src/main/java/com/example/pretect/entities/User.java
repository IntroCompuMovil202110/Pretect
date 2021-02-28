package com.example.pretect.entities;

import com.example.pretect.AbstractClass.PictureName;
import com.example.pretect.interfaces.ListablePictureName;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends PictureName implements Serializable {
    private String user_name;
    private String password;
    private String email;
    private String bait_phrase;
    private String safety_phrase;
    private int profile_picture;
    private boolean state;

    private ArrayList<User> contacts;
    private ArrayList<Recording> recordings;
    private Location location;

    public User(String user_name, String email, int profile_picture ) {
        this.user_name = user_name;
        this.email = email;
        this.profile_picture = profile_picture;
    }

    @Override
    public String getName() {
        return user_name;
    }

    @Override
    public int getPicture() {
        return profile_picture;
    }
}

