package com.example.pretect.entities.chatEntities;

public class FriendlyMessage {

    private String text;
    private String name;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name) {
        this.text = text;
        this.name = name;

    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

}
