package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 01/08/17.
 */

public class Genre {

    private String name;
    private String backgroundImage;

    public Genre(String name, String backgroundImage) {
        this.name = name;
        this.backgroundImage = backgroundImage;
    }

    public String getName() {
        return name;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
}
