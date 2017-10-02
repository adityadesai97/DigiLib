package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 02/10/17.
 */

public class FeedEvent {

    private String genreName;
    private String name;
    private String profile_id;
    private String bookId;
    private String purchaseType;
    private String timestamp;

    public FeedEvent(String genreName, String name, String profile_id, String bookId, String purchaseType, String timestamp) {
        this.genreName = genreName;
        this.name = name;
        this.profile_id = profile_id;
        this.bookId = bookId;
        this.purchaseType = purchaseType;
        this.timestamp = timestamp;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getName() {
        return name;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
