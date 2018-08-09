package com.ruslaniusupov.achievity.data.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalRepository implements GoalDataSource, LifecycleObserver {

    private static final String TAG = GoalRepository.class.getSimpleName();

    private static final String GOALS = "goals";
    private static final String USERS_DATA = "users-data";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String GOAL = "goal";
    private static final String USERS_LIKED = "users-liked";
    private static final String SUBSCRIBERS = "subscribers";
    private static final String METRICS = "metrics";
    private static final String FIELD_LIKES_COUNT = "likesCount";
    private static final String FIELD_SUBSCRIBERS_COUNT = "subscribersCount";
    private static final String FIELD_NOTES_COUNT = "notesCount";
    private static final String FIELD_AUTHOR_ID = "authorId";
    private static final String FIELD_GOAL_ID = "goalId";

    private final DatabaseReference mDb;
    private final DatabaseReference mGoalsDataRef;
    private Query mUserGoalsQuery;
    private ValueEventListener mUserGoalsListener;

    public GoalRepository() {
        mDb = FirebaseDatabase.getInstance().getReference();
        mGoalsDataRef = mDb.child(GOALS);
    }

    @Override
    public void getUsersLiked(@NonNull String goalId, @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener usersLikedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userIds.add(snap.getKey());
                }
                callback.onDataLoaded(userIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(USERS_LIKED)
                .addListenerForSingleValueEvent(usersLikedListener);
    }

    @Override
    public void getSubscribers(@NonNull String goalId, @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener subscribersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userIds.add(snap.getKey());
                }
                callback.onDataLoaded(userIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(SUBSCRIBERS)
                .addListenerForSingleValueEvent(subscribersListener);
    }

    @Override
    public void getGoals(@NonNull String authorId, @NonNull final DataCallback<List<Goal>> callback) {
        mUserGoalsQuery = mGoalsDataRef.orderByChild(GOAL + "/" + FIELD_AUTHOR_ID).equalTo(authorId);
        mUserGoalsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange");
                ArrayList<Goal> goals = new ArrayList<>();
                for (DataSnapshot goalSnap : dataSnapshot.getChildren()) {
                    goals.add(goalSnap.child(GOAL).getValue(Goal.class));
                }
                callback.onDataLoaded(goals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
    }

    @Override
    public void getMostLikedGoals(@NonNull final DataCallback<List<Goal>> callback) {
        mGoalsDataRef.orderByChild(METRICS + "/" + FIELD_LIKES_COUNT)
                .limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Goal> goals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    goals.add(snapshot.child(GOAL).getValue(Goal.class));
                }
                Collections.reverse(goals);
                callback.onDataLoaded(goals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        });
    }

    @Override
    public void getRecentGoals(@NonNull final DataCallback<List<Goal>> callback) {
        mGoalsDataRef.limitToFirst(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Goal> goals = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            goals.add(snapshot.child(GOAL).getValue(Goal.class));
                        }
                        Collections.reverse(goals);
                        callback.onDataLoaded(goals);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onDataNotAvailable(databaseError.toException());
                    }
                });
    }

    @Override
    public void getGoal(@NonNull String goalId, @NonNull final DataCallback<Goal> callback) {
        mGoalsDataRef.child(goalId).child(GOAL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.onDataLoaded(dataSnapshot.getValue(Goal.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onDataNotAvailable(databaseError.toException());
                    }
                });
    }

    @Override
    public void getLikesCount(@NonNull String goalId, @NonNull final DataCallback<Long> callback) {
        ValueEventListener likesCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded((Long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_LIKES_COUNT)
                .addListenerForSingleValueEvent(likesCountListener);
    }

    @Override
    public void getSubscribersCount(@NonNull String goalId, @NonNull final DataCallback<Long> callback) {
        ValueEventListener subscribersCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded((Long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_SUBSCRIBERS_COUNT)
                .addListenerForSingleValueEvent(subscribersCountListener);
    }

    @Override
    public void getNotesCount(@NonNull String goalId, @NonNull final DataCallback<Long> callback) {
        ValueEventListener notesCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded((Long) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_NOTES_COUNT)
                .addListenerForSingleValueEvent(notesCountListener);
    }

    @Override
    public void addUserLiked(@NonNull final String goalId,
                             @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        Map<String, Object> data = new HashMap<>();
        String pathLikedUser = GOALS + "/" + goalId + "/" + USERS_LIKED + "/" + userLikedId;
        String pathLikedGoal = "/users-data/" + userLikedId + "/goals-liked/" + goalId;
        data.put(pathLikedGoal, true);
        data.put(pathLikedUser, true);
        mDb.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_LIKES_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long counter = (Long) mutableData.getValue();
                                        if (counter == null) {
                                            mutableData.setValue(1L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(counter + 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                        if (databaseError != null) {
                                            callback.onFailure(databaseError.toException());
                                        } else {
                                            callback.onSuccess();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public void addSubscriber(@NonNull final String goalId, @NonNull final String subscriberId, @NonNull final StatusCallback callback) {
        String pathSubscriber = GOALS + "/" + goalId + "/" + SUBSCRIBERS + "/" + subscriberId;
        String pathSubscription = "/users-data/" + subscriberId + "/subscriptions/" + goalId;

        Map<String, Object> update = new HashMap<>();
        update.put(pathSubscriber, true);
        update.put(pathSubscription, true);

        mDb.updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_SUBSCRIBERS_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long counter = (Long) mutableData.getValue();
                                        if (counter == null) {
                                            mutableData.setValue(1L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(counter + 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                        if (databaseError != null) {
                                            callback.onFailure(databaseError.toException());
                                        } else {
                                            callback.onSuccess();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public void addGoal(@NonNull final Goal goal, @NonNull final StatusCallback callback) {
        String goalId = mGoalsDataRef.push().getKey();
        goal.setId(goalId);
        final DatabaseReference ref = mGoalsDataRef.child(goalId).child(GOAL);
        ref.setValue(goal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ref.child(Goal.FIELD_TIMESTAMP).setValue(ServerValue.TIMESTAMP);
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });

    }

    @Override
    public void deleteUserLiked(@NonNull final String goalId, @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        String pathLikedUser = GOALS + "/" + goalId + "/" + USERS_LIKED + "/" + userLikedId;
        String pathLikedGoal = "/users-data/" + userLikedId + "/goals-liked/" + goalId;
        Map<String, Object> data = new HashMap<>();
        data.put(pathLikedGoal, null);
        data.put(pathLikedUser, null);
        mDb.updateChildren(data)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_LIKES_COUNT)
                            .runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    Long counter = (Long) mutableData.getValue();
                                    if (counter == null) {
                                        mutableData.setValue(0L);
                                        return Transaction.success(mutableData);
                                    }
                                    mutableData.setValue(counter - 1);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    if (databaseError != null) {
                                        callback.onFailure(databaseError.toException());
                                    } else {
                                        callback.onSuccess();
                                    }
                                }
                            });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onFailure(e);
                }
            });
    }

    @Override
    public void deleteSubscriber(@NonNull final String goalId,
                                 @NonNull final String subscriberId, @NonNull final StatusCallback callback) {
        String pathSubscriber = GOALS + "/" + goalId + "/" + SUBSCRIBERS + "/" + subscriberId;
        String pathSubscription = "/users-data/" + subscriberId + "/subscriptions/" + goalId;
        Map<String, Object> data = new HashMap<>();
        data.put(pathSubscriber, null);
        data.put(pathSubscription, null);
        mDb.updateChildren(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mGoalsDataRef.child(goalId).child(METRICS).child(FIELD_SUBSCRIBERS_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long counter = (Long) mutableData.getValue();
                                        if (counter == null) {
                                            mutableData.setValue(0L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(counter - 1);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                        if (databaseError != null) {
                                            callback.onFailure(databaseError.toException());
                                        } else {
                                            callback.onSuccess();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });

    }

    @Override
    public void updateText(@NonNull String goalId,
                           @NonNull String newText, @NonNull final StatusCallback callback) {
        mGoalsDataRef.child(goalId).child(GOAL).child(Goal.FIELD_TEXT)
                .setValue(newText)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public void updateStatus(@NonNull String goalId,
                             boolean isCompleted, @NonNull final StatusCallback callback) {
        mGoalsDataRef.child(goalId).child(GOAL).child(Goal.FIELD_COMPLETED)
                .setValue(isCompleted)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    @Override
    public void isLiked(@NonNull String goalId, @NonNull String userId, @NonNull final DataCallback<Boolean> callback) {
        ValueEventListener isLikedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    callback.onDataLoaded(false);
                } else {
                    callback.onDataLoaded(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(USERS_LIKED).child(userId)
                .addListenerForSingleValueEvent(isLikedListener);
    }

    @Override
    public void isSubscribed(@NonNull String goalId, @NonNull String userId, @NonNull final DataCallback<Boolean> callback) {
        ValueEventListener isSubscribedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    callback.onDataLoaded(false);
                } else {
                    callback.onDataLoaded(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mGoalsDataRef.child(goalId).child(SUBSCRIBERS).child(userId)
                .addListenerForSingleValueEvent(isSubscribedListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void startListening() {
        if (mUserGoalsQuery != null && mUserGoalsListener != null) {
            mUserGoalsQuery.addValueEventListener(mUserGoalsListener);
            Log.d(TAG, "User's goals listener added.");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stopListening() {
        if (mUserGoalsQuery != null && mUserGoalsListener != null) {
            mUserGoalsQuery.removeEventListener(mUserGoalsListener);
            Log.d(TAG, "User's goals listener removed.");
        }
    }

}
