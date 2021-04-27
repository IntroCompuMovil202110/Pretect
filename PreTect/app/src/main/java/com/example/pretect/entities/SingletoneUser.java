package com.example.pretect.entities;

import android.media.MediaRecorder;

public class SingletoneUser {

    User user;
    MediaRecorder recorder;
    private static final SingletoneUser instance = new SingletoneUser();

    public static SingletoneUser getInstance(){
        return instance;
    }

    private SingletoneUser(){
    }

    public void setData(User user){
        this.user = user;
    }

    public User getData(){
        return user;
    }

    public MediaRecorder getRecorder() {
        return recorder;
    }

    public void setRecorder(MediaRecorder recorder) {
        this.recorder = recorder;
    }
}
