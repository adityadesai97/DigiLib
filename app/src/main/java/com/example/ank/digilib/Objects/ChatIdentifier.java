package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 22/10/17.
 */

public class ChatIdentifier {

    private String uid1;
    private String uid2;

    public ChatIdentifier(String uid1, String uid2) {
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    public String getUid1() {
        return uid1;
    }

    public String getUid2() {
        return uid2;
    }
}
