package com.ruslaniusupov.achievity.data.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;


@Keep
public class Note implements Parcelable {

    public static final String FIELD_ID = "id";
    public static final String FIELD_AUTHOR_ID = "authorId";
    public static final String FIELD_GOAL_ID = "goalId";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_TIMESTAMP = "timestamp";

    private String id;
    private String authorId;
    private String goalId;
    private String author;
    private String text;
    private long timestamp;

    public Note() {
    }

    public Note(String authorId, String goalId, String author, String text) {
        this.authorId = authorId;
        this.goalId = goalId;
        this.author = author;
        this.text = text;
    }

    public Note(String id, String authorId, String goalId, String author, String text, long timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.goalId = goalId;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    protected Note(Parcel in) {
        id = in.readString();
        authorId = in.readString();
        goalId = in.readString();
        author = in.readString();
        text = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

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

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(authorId);
        dest.writeString(goalId);
        dest.writeString(author);
        dest.writeString(text);
        dest.writeLong(timestamp);
    }
}
