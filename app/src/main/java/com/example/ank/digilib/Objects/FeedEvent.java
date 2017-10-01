package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 02/10/17.
 */

public class FeedEvent {

    private String userId;
    private String bookId;
    private String purchaseType;
    private String timestamp;

    public FeedEvent(String userId, String bookId, String purchaseType, String timestamp) {
        this.userId = userId;
        this.bookId = bookId;
        this.purchaseType = purchaseType;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
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
