package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.model.User;

import java.util.List;

public interface UserDataDataSource {

    interface UsernameUpdateCallback  {

        void onUsernameUpdated();

        void onUsernameExists();

        void onError(Exception e);

    }

    void getGoalsLiked(@NonNull String userId, @NonNull DataCallback<List<String>> callback);

    void getNotesLiked(@NonNull String userId, @NonNull DataCallback<List<String>> callback);

    void getSubscriptions(@NonNull String userId, @NonNull DataCallback<List<String>> callback);

    void getUser(@NonNull String userId, @NonNull DataCallback<User> callback);

    void getName(@NonNull String userId, @NonNull DataCallback<String> callback);

/*    void addGoalLiked(@NonNull String userId, @NonNull String goalId, @NonNull StatusCallback callback);

    void addNoteLiked(@NonNull String userId, @NonNull String noteId, @NonNull StatusCallback callback);

    void addSubscription(@NonNull String userId, @NonNull String goalId, @NonNull StatusCallback callback);*/

    void addUser(@NonNull User user, @NonNull StatusCallback callback);

/*    void deleteGoalLiked(@NonNull String userId, @NonNull String goalId, @NonNull StatusCallback callback);

    void deleteNoteLiked(@NonNull String userId, @NonNull String noteId, @NonNull StatusCallback callback);

    void deleteSubscription(@NonNull String userId, @NonNull String goalId, @NonNull StatusCallback callback);*/

    void deleteUser(@NonNull String userId, @NonNull StatusCallback callback);

    void updateName(@NonNull String userId, @NonNull String newName, @NonNull StatusCallback callback);

    void updateBio(@NonNull String userId, @NonNull String newBio, @NonNull StatusCallback callback);

    void updateUsername(@NonNull String userId, @NonNull String newUsername, @NonNull UsernameUpdateCallback callback);

}
