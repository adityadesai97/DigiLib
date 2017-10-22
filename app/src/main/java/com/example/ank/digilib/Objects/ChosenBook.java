package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 02/09/17.
 */

public class ChosenBook {
    private String key;
    private String genreName;
    private String purchaseType;
    private String timestamp;
    private String fileName;

    public ChosenBook(String key, String genreName, String purchaseType, String timestamp, String fileName) {
        this.key = key;
        this.genreName = genreName;
        this.purchaseType = purchaseType;
        this.timestamp = timestamp;
        this.fileName = fileName;
    }

    public ChosenBook() {
        this.key = "dummy";
        this.genreName = "dummy";
        this.purchaseType = "dummy";
        this.timestamp = "dummy";
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

    public String getTimestamp() {
        return timestamp;
    }

    public String getFileName() {
        return fileName;
    }
}
