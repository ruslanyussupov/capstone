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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.ruslaniusupov.achievity.data.CommentDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentRepository implements CommentDataSource, LifecycleObserver {

    private static final String COMMENTS = "comments";
    private static final String NOTES = "notes";
    private static final String COMMENT = "comment";
    private static final String METRICS = "metrics";
    private static final String USERS_LIKED = "users-liked";
    private static final String FIELD_LIKES_COUNT = "likesCount";
    private static final String FIELD_COMMENTS_COUNT = "commentsCount";

    private final DatabaseReference mDb;
    private final DatabaseReference mCommentsRef;
    private ValueEventListener mCommentsListener;
    private DatabaseReference mNoteCommentsRef;

    public CommentRepository() {
        mDb = FirebaseDatabase.getInstance().getReference();
        mCommentsRef = mDb.child(COMMENTS);
    }

    @Override
    public void getUsersLiked(@NonNull String noteId, @NonNull String commentId, @NonNull final DataCallback<List<String>> callback) {
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
        mCommentsRef.child(noteId).child(commentId).child(USERS_LIKED).addListenerForSingleValueEvent(usersLikedListener);
    }

    @Override
    public void getComments(@NonNull String noteId, @NonNull final DataCallback<List<Comment>> callback) {
        mNoteCommentsRef = mCommentsRef.child(noteId);
        mCommentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    comments.add(snap.child(COMMENT).getValue(Comment.class));
                }
                callback.onDataLoaded(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
    }

    @Override
    public void getComment(@NonNull String noteId, @NonNull String commentId, @NonNull final DataCallback<Comment> callback) {
        ValueEventListener commentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded(dataSnapshot.getValue(Comment.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mCommentsRef.child(noteId).child(commentId).child(COMMENT).addListenerForSingleValueEvent(commentListener);
    }

    @Override
    public void getLikesCount(@NonNull String noteId, @NonNull String commentId, @NonNull final DataCallback<Long> callback) {
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
        mCommentsRef.child(noteId).child(commentId).child(METRICS).child(FIELD_LIKES_COUNT)
                .addListenerForSingleValueEvent(likesCountListener);
    }

    @Override
    public void addUserLiked(@NonNull final String noteId, @NonNull final String commentId, @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        mCommentsRef.child(noteId).child(commentId).child(METRICS).child(FIELD_LIKES_COUNT)
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
                            mCommentsRef.child(noteId).child(commentId).child(USERS_LIKED).child(userLikedId)
                                    .setValue(true)
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
                    }
                });
    }

    @Override
    public void addComment(@NonNull final Comment comment, @NonNull final StatusCallback callback) {
        final DatabaseReference commentRef = mCommentsRef.child(comment.getNoteId()).push();
        comment.setId(commentRef.getKey());
        commentRef.child(COMMENT).setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        commentRef.child(COMMENT).child(Comment.FIELD_TIMESTAMP)
                                .setValue(ServerValue.TIMESTAMP);
                        mDb.child(NOTES).child(comment.getNoteId()).child(METRICS).child(FIELD_COMMENTS_COUNT)
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
    public void updateText(@NonNull String noteId, @NonNull String commentId, @NonNull String newText, @NonNull final StatusCallback callback) {
        mCommentsRef.child(noteId).child(commentId).child(COMMENT).child(Comment.FIELD_TEXT)
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
    public void deleteUserLiked(@NonNull final String noteId, @NonNull final String commentId, @NonNull final String userLikedId, @NonNull final StatusCallback callback) {
        mCommentsRef.child(noteId).child(commentId).child(METRICS).child(FIELD_LIKES_COUNT)
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
                            mCommentsRef.child(noteId).child(commentId).child(USERS_LIKED).child(userLikedId)
                                    .removeValue()
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
                    }
                });
    }

    @Override
    public void deleteComment(@NonNull final String noteId, @NonNull String commentId, @NonNull final StatusCallback callback) {
        mCommentsRef.child(noteId).child(commentId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDb.child(NOTES).child(noteId).child(METRICS).child(FIELD_COMMENTS_COUNT)
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
    public void isLiked(@NonNull String noteId, @NonNull String commentId, @NonNull String userId, @NonNull final DataCallback<Boolean> callback) {
        mCommentsRef.child(noteId).child(commentId).child(USERS_LIKED).child(userId)
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
        if (mNoteCommentsRef != null && mCommentsListener != null) {
            mNoteCommentsRef.addValueEventListener(mCommentsListener);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopListening() {
        if (mNoteCommentsRef != null && mCommentsListener != null) {
            mNoteCommentsRef.removeEventListener(mCommentsListener);
        }
    }

}
