package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.model.Comment;

import java.util.List;

public interface CommentDataSource {

    void getUsersLiked(@NonNull String noteId, @NonNull String commentId, @NonNull DataCallback<List<String>> callback);

    void getComments(@NonNull String noteId, @NonNull DataCallback<List<Comment>> callback);

    void getComment(@NonNull String noteId, @NonNull String commentId, @NonNull DataCallback<Comment> callback);

    void getLikesCount(@NonNull String noteId, @NonNull String commentId, @NonNull DataCallback<Long> callback);

    void addUserLiked(@NonNull String noteId, @NonNull String commentId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void addComment(@NonNull Comment comment, @NonNull StatusCallback callback);

    void updateText(@NonNull String noteId, @NonNull String commentId, @NonNull String newText, @NonNull StatusCallback callback);

    void deleteUserLiked(@NonNull String noteId, @NonNull String commentId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void deleteComment(@NonNull String noteId, @NonNull String commentId, @NonNull StatusCallback callback);

    void isLiked(@NonNull String noteId, @NonNull String commentId, @NonNull String userId, @NonNull DataCallback<Boolean> callback);

}
