package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import com.ruslaniusupov.achievity.data.model.Goal;

import java.util.List;

public interface GoalDataSource {

    void getUsersLiked(@NonNull String goalId, @NonNull DataCallback<List<String>> callback);

    void getSubscribers(@NonNull String goalId, @NonNull DataCallback<List<String>> callback);

    void getGoals(@NonNull String authorId, @NonNull DataCallback<List<Goal>> callback);

    void getMostLikedGoals(@NonNull DataCallback<List<Goal>> callback);

    void getRecentGoals(@NonNull DataCallback<List<Goal>> callback);

    void getGoal(@NonNull String goalId, @NonNull DataCallback<Goal> callback);

    void getLikesCount(@NonNull String goalId, @NonNull DataCallback<Long> callback);

    void getSubscribersCount(@NonNull String goalId, @NonNull DataCallback<Long> callback);

    void getNotesCount(@NonNull String goalId, @NonNull DataCallback<Long> callback);

    void addUserLiked(@NonNull String goalId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void addSubscriber(@NonNull String goalId, @NonNull String subscriberId, @NonNull StatusCallback callback);

    void addGoal(@NonNull Goal goal, @NonNull StatusCallback callback);

    void deleteUserLiked(@NonNull String goalId, @NonNull String userLikedId, @NonNull StatusCallback callback);

    void deleteSubscriber(@NonNull String goalId, @NonNull String subscriberId, @NonNull StatusCallback callback);

    void updateText(@NonNull String goalId, @NonNull String newText, @NonNull StatusCallback callback);

    void updateStatus(@NonNull String goalId, boolean isCompleted, @NonNull StatusCallback callback);

    void isLiked(@NonNull String goalId, @NonNull String userId, @NonNull DataCallback<Boolean> callback);

    void isSubscribed(@NonNull String goalId, @NonNull String userId, @NonNull DataCallback<Boolean> callback);

}
