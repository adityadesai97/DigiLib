package com.example.ank.digilib.Objects;

/**
 * Created by adityadesai on 31/07/17.
 */

public class User {
    private String name;
    private String uid;
    private String mail_id;
    private String profile_url;
    private String credits;

    public User(String name, String uid, String mail_id, String profile_url, String credits) {
        this.name = name;
        this.uid = uid;
        this.mail_id = mail_id;
        this.profile_url = profile_url;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getMail_id() {
        return mail_id;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public String getCredits() {
        return credits;
    }
}