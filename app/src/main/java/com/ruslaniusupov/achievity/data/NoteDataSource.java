package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.model.Note;

import java.util.List;

public interface NoteDataSource {

    void getUsersLiked(@NonNull String noteId, @NonNull DataCallback<List<String>> callback);

    void getNotes(@NonNull String goalId, @NonNull DataCallback<List<Note>> callback);

    void getNote(@NonNull String noteId, @NonNull DataCallback<Note> callback);

    void getLikesCount(@NonNull String noteId, @NonNull DataCallback<Long> callback);

    void getCommentsCount(@NonNull String noteId, @NonNull DataCallback<Long> callback);

    void addUserLiked(@NonNull String noteId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void addNote(@NonNull Note note, @NonNull StatusCallback callback);

    void updateText(@NonNull String noteId, @NonNull String newText, @NonNull StatusCallback callback);

    void deleteUserLiked(@NonNull String noteId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void deleteNote(@NonNull String noteId, @NonNull String goalId, @NonNull StatusCallback callback);

    void isLiked(@NonNull String noteId, @NonNull String userId, @NonNull DataCallback<Boolean> callback);

}
