package com.ruslaniusupov.achievity.data.model;

import android.support.annotation.Keep;


@Keep
public class Comment {

    public static final String FIELD_ID = "id";
    public static final String FIELD_AUTHOR_ID = "authorId";
    public static final String FIELD_NOTE_ID = "noteId";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_TIMESTAMP = "timestamp";

    private String id;
    private String authorId;
    private String noteId;
    private String text;
    private long timestamp;

    public Comment() {
    }

    public Comment(String authorId, String noteId, String text) {
        this.authorId = authorId;
        this.noteId = noteId;
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

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
