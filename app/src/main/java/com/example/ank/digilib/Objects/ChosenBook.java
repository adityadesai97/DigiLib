package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 02/09/17.
 */

public class ChosenBook {
    private String key;
    private String genreName;
    private String purchaseType;

    public ChosenBook(String key, String genreName, String purchaseType) {
        this.key = key;
        this.genreName = genreName;
        this.purchaseType = purchaseType;
    }

    public ChosenBook() {
        this.key = "dummy";
        this.genreName = "dummy";
        this.purchaseType = "dummy";
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
