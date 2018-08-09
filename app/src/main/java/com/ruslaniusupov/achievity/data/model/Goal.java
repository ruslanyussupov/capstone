package com.ruslaniusupov.achievity.data.model;

import android.support.annotation.Keep;


@Keep
public class Goal {

    public static final String FIELD_ID = "id";
    public static final String FIELD_AUTHOR_ID = "authorId";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_COMPLETED = "completed";
    public static final String FIELD_TIMESTAMP = "timestamp";

    private String id;
    private String authorId;
    private String text;
    private boolean completed;
    private long timestamp;

    public Goal() {
    }

    public Goal(String authorId, String text) {
        this.authorId = authorId;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
