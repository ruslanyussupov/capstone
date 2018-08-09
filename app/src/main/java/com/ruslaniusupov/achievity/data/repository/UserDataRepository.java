package com.ruslaniusupov.achievity.data.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDataRepository implements UserDataDataSource, LifecycleObserver {

    private static final String TAG = UserDataRepository.class.getSimpleName();

    private static final String USERS_DATA = "users-data";
    private static final String GOALS_LIKED = "goals-liked";
    private static final String NOTES_LIKED = "notes-liked";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String USERNAMES = "usernames";
    private static final String USER = "user";

    private final DatabaseReference mDb;
    private final DatabaseReference mUsersDataRef;
    private DatabaseReference mUserRef;
    private ValueEventListener mUserListener;

    public UserDataRepository() {
        mDb = FirebaseDatabase.getInstance().getReference();
        mUsersDataRef = mDb.child(USERS_DATA);
    }

    @Override
    public void getGoalsLiked(@NonNull String userId, @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener goalsLikedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> goalIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    goalIds.add(snap.getKey());
                }
                callback.onDataLoaded(goalIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mUsersDataRef.child(userId).child(GOALS_LIKED).addListenerForSingleValueEvent(goalsLikedListener);
    }

    @Override
    public void getNotesLiked(@NonNull String userId, @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener notesLikedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> notesIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    notesIds.add(snap.getKey());
                }
                callback.onDataLoaded(notesIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mUsersDataRef.child(userId).child(NOTES_LIKED).addListenerForSingleValueEvent(notesLikedListener);
    }

    @Override
    public void getSubscriptions(@NonNull String userId, @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener subscriptionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> goalsIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    goalsIds.add(snap.getKey());
                }
                callback.onDataLoaded(goalsIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mUsersDataRef.child(userId).child(SUBSCRIPTIONS).addListenerForSingleValueEvent(subscriptionsListener);
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull final DataCallback<User> callback) {
        mUserRef = mUsersDataRef.child(userId).child(USER);
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
    }

    @Override
    public void getName(@NonNull String userId, @NonNull final DataCallback<String> callback) {
        mUsersDataRef.child(userId).child(USER).child(User.FIELD_NAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.onDataLoaded( (String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onDataNotAvailable(databaseError.toException());
                    }
                });
    }


/*    @Override
    public void addGoalLiked(@NonNull String userId, @NonNull String goalId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(GOALS_LIKED).child(goalId).setValue(true)
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
    public void addNoteLiked(@NonNull String userId, @NonNull String noteId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(NOTES_LIKED).child(noteId).setValue(true)
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
    public void addSubscription(@NonNull String userId, @NonNull String goalId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(SUBSCRIPTIONS).child(goalId).setValue(true)
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
    }*/

    @Override
    public void addUser(@NonNull final User user, @NonNull final StatusCallback callback) {
        mUsersDataRef.child(user.getId()).child(USER).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mUsersDataRef.child(user.getId()).child(USER).child(User.FIELD_TIMESTAMP)
                                .setValue(ServerValue.TIMESTAMP);
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

/*    @Override
    public void deleteGoalLiked(@NonNull String userId, @NonNull String goalId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(GOALS_LIKED).child(goalId).removeValue()
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
    public void deleteNoteLiked(@NonNull String userId, @NonNull String noteId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(NOTES_LIKED).child(noteId).removeValue()
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
    public void deleteSubscription(@NonNull String userId, @NonNull String goalId, @NonNull final StatusCallback callback) {
        mDb.child(userId).child(SUBSCRIPTIONS).child(goalId).removeValue()
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
    }*/

    @Override
    public void deleteUser(@NonNull String userId, @NonNull final StatusCallback callback) {
        mUsersDataRef.child(userId).removeValue()
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
    public void updateName(@NonNull String userId, @NonNull String newName, @NonNull final StatusCallback callback) {
        mUsersDataRef.child(userId).child(USER).child(User.FIELD_NAME).setValue(newName)
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
    public void updateBio(@NonNull String userId, @NonNull String newBio, @NonNull final StatusCallback callback) {
        mUsersDataRef.child(userId).child(USER).child(User.FIELD_BIO).setValue(newBio)
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
    public void updateUsername(@NonNull final String userId, @NonNull final String newUsername, @NonNull final UsernameUpdateCallback callback) {
        ValueEventListener usernameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    mDb.child(USERNAMES).child(newUsername).setValue(true);
                    mUsersDataRef.child(userId).child(USER).child(User.FIELD_USERNAME).setValue(newUsername)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.onUsernameUpdated();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callback.onError(e);
                                }
                            });
                } else {
                    callback.onUsernameExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        };
        mDb.child(USERNAMES).child(newUsername).addListenerForSingleValueEvent(usernameListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void startListening() {
        if (mUserRef != null && mUserListener != null) {
            mUserRef.addValueEventListener(mUserListener);
            Log.d(TAG, "User listener added.");
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stopListening() {
        if (mUserRef != null && mUserListener != null) {
            mUserRef.removeEventListener(mUserListener);
            Log.d(TAG, "User listener removed.");
        }
    }

}
