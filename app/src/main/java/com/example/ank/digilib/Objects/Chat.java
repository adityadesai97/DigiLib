package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 22/10/17.
 */

public class Chat {

    private String text;
    private String name;

    public Chat(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
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
}
