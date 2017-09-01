package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 02/09/17.
 */

public class ChosenBook {
    private String key;
    private String genreName;
    private String purchaseType;

    public ChosenBook(String key, String genre, String purchaseType) {
        this.key = key;
        this.genreName = genre;
        this.purchaseType = purchaseType;
    }

    public String getKey() {
        return key;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getPurchaseType() {
        return purchaseType;
    }
}
