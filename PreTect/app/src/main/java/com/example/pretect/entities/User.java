package com.example.pretect.entities;

import android.app.Application;

import com.example.pretect.AbstractClass.PictureName;
import com.example.pretect.R;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends PictureName implements Serializable {
    private String name;
    private String password;
    private String email;
    private String bait_phrase;
    private String safety_phrase;
    private String phone;
    private String emergency_contact;
    private String profile_picture;
    private boolean state;
    private int age;
    private ArrayList<User> contacts;
    private ArrayList<Recording> recordings;
    private Location location;
    private boolean grabarAudio;

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public User(String name, String email, boolean state, int age) {
        this.name = name;
        this.email = email;
        this.state = state;
        this.age = age;
    }

    public User() {
    }

    public boolean isGrabarAudio() {
        return grabarAudio;
    }

    public void setGrabarAudio(boolean grabarAudio) {
        this.grabarAudio = grabarAudio;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return this.name;
    }

    @Override
    public String getName() {
        return null;
    }




    @Override
    public String getPicture() {
        return profile_picture;
    }

    public String getEmail() {
        return email;
    }

    public String getBait_phrase() {
        return bait_phrase;
    }

    public String getSafety_phrase() {
        return safety_phrase;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmergencyContact() {
        return emergency_contact;
    }

    public boolean isState() {
        return state;
    }

    public void setBait_phrase(String bait_phrase) {
        this.bait_phrase = bait_phrase;
    }

    public void setSafety_phrase(String safety_phrase) {
        this.safety_phrase = safety_phrase;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergency_contact = emergencyContact;
    }

   /* static public ArrayList<User> getContacts(){
        ArrayList<User> contacts = new ArrayList<>();
        contacts.add(new User("Sebastian","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Daniel","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Tibaquira","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Rodrigo","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Jorge","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Angarita","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Ana","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Maria","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Carlos","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Daniel Javeriana","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Paola","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Natalia","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Laura","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Amor","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Daniela","user@email.com", R.drawable.photo_placeholder));
        contacts.add(new User("Karen","user@email.com", R.drawable.photo_placeholder));

        return contacts;
    }*/
}

