package com.ruslaniusupov.achievity.data.model;


import android.support.annotation.Keep;


@Keep
public class User {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_BIO = "bio";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_TIMESTAMP = "timestamp";

    private String id;
    private String name;
    private String bio;
    private String username;
    private long timestamp;

    public User() {}

    public User(String id, String name, String bio, String username) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.username = username;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
