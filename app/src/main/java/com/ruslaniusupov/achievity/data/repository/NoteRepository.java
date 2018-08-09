package com.ruslaniusupov.achievity.data.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteRepository implements NoteDataSource, LifecycleObserver {

    private static final String NOTES = "notes";
    private static final String GOALS = "goals";
    private static final String USERS_DATA = "users-data";
    private static final String USERS_LIKED = "users-liked";
    private static final String NOTES_LIKED = "notes-liked";
    private static final String METRICS = "metrics";
    private static final String NOTE = "note";
    private static final String FIELD_LIKES_COUNT = "likesCount";
    private static final String FIELD_COMMENTS_COUNT = "commentsCount";
    private static final String FIELD_NOTES_COUNT = "notesCount";

    private final DatabaseReference mDb;
    private final DatabaseReference mNotesRef;
    private Query mNotesQuery;
    private ValueEventListener mNotesEventListener;

    public NoteRepository() {
        mDb = FirebaseDatabase.getInstance().getReference();
        mNotesRef = mDb.child(NOTES);
    }

    @Override
    public void getUsersLiked(@NonNull String noteId,
                              @NonNull final DataCallback<List<String>> callback) {
        ValueEventListener usersLikedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> usersIds = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    usersIds.add(snap.getKey());
                }
                callback.onDataLoaded(usersIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mNotesRef.child(noteId).child(USERS_LIKED)
                .addListenerForSingleValueEvent(usersLikedListener);
    }

    @Override
    public void getNotes(@NonNull String goalId, @NonNull final DataCallback<List<Note>> callback) {
        mNotesQuery = mNotesRef.orderByChild(NOTE + "/" + Note.FIELD_GOAL_ID).equalTo(goalId);
        mNotesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Note> notes = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    notes.add(snap.child(NOTE).getValue(Note.class));
                }
                callback.onDataLoaded(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mNotesQuery.addValueEventListener(mNotesEventListener);
    }

    @Override
    public void getNote(@NonNull String noteId, @NonNull final DataCallback<Note> callback) {
        ValueEventListener noteListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded(dataSnapshot.getValue(Note.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mNotesRef.child(noteId).child(NOTE)
                .addListenerForSingleValueEvent(noteListener);
    }

    @Override
    public void getLikesCount(@NonNull String noteId, @NonNull final DataCallback<Long> callback) {
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
        mNotesRef.child(noteId).child(METRICS).child(FIELD_LIKES_COUNT)
                .addListenerForSingleValueEvent(likesCountListener);
    }

    @Override
    public void getCommentsCount(@NonNull String noteId, @NonNull final DataCallback<Long> callback) {
        mNotesRef.child(noteId).child(METRICS).child(FIELD_COMMENTS_COUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.onDataLoaded((Long) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onDataNotAvailable(databaseError.toException());
                    }
                });
    }

    @Override
    public void addUserLiked(@NonNull final String noteId, @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        String pathUserLiked = NOTES + "/" + noteId + "/" + USERS_LIKED + "/" + userLikedId;
        String pathNoteLiked = USERS_DATA + "/" + userLikedId + "/" + NOTES_LIKED + "/" + noteId;
        Map<String, Object> update = new HashMap<>();
        update.put(pathUserLiked, true);
        update.put(pathNoteLiked, true);
        mDb.updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mNotesRef.child(noteId).child(METRICS).child(FIELD_LIKES_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long count = (Long) mutableData.getValue();
                                        if (count == null) {
                                            mutableData.setValue(1L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(count + 1);
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
                });
    }

    @Override
    public void addNote(@NonNull final Note note, @NonNull final StatusCallback callback) {
        final DatabaseReference noteRef = mNotesRef.push();
        note.setId(noteRef.getKey());
        noteRef.child(NOTE).setValue(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        noteRef.child(NOTE).child(Note.FIELD_TIMESTAMP)
                                .setValue(ServerValue.TIMESTAMP);
                        mDb.child(GOALS).child(note.getGoalId()).child(METRICS).child(FIELD_NOTES_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long count = (Long) mutableData.getValue();
                                        if (count == null) {
                                            mutableData.setValue(1L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(count + 1);
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
    public void updateText(@NonNull String noteId,
                           @NonNull String newText, @NonNull final StatusCallback callback) {
        mNotesRef.child(noteId).child(NOTE).child(Note.FIELD_TEXT)
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
    public void deleteUserLiked(@NonNull final String noteId,
                                @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        String pathUserLiked = NOTES + "/" + noteId + "/" + USERS_LIKED + "/" + userLikedId;
        String pathNoteLiked = USERS_DATA + "/" + userLikedId + "/" + NOTES_LIKED + "/" + noteId;
        Map<String, Object> update = new HashMap<>();
        update.put(pathUserLiked, null);
        update.put(pathNoteLiked, null);
        mDb.updateChildren(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mNotesRef.child(noteId).child(METRICS).child(FIELD_LIKES_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long count = (Long) mutableData.getValue();
                                        if (count == null) {
                                            mutableData.setValue(0L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(count - 1);
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
                });

    }

    @Override
    public void deleteNote(@NonNull String noteId, @NonNull final String goalId,
                           @NonNull final StatusCallback callback) {
        mNotesRef.child(noteId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDb.child(GOALS).child(goalId).child(METRICS).child(FIELD_NOTES_COUNT)
                                .runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                        Long count = (Long) mutableData.getValue();
                                        if (count == null) {
                                            mutableData.setValue(0L);
                                            return Transaction.success(mutableData);
                                        }
                                        mutableData.setValue(count - 1);
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
    public void isLiked(@NonNull String noteId, @NonNull String userId, @NonNull final DataCallback<Boolean> callback) {
        mNotesRef.child(noteId).child(USERS_LIKED).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void startListening() {
        if (mNotesQuery != null && mNotesEventListener != null) {
            mNotesQuery.addValueEventListener(mNotesEventListener);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopListening() {
        if (mNotesQuery != null && mNotesEventListener != null) {
            mNotesQuery.removeEventListener(mNotesEventListener);
        }
    }
}
