package com.ruslaniusupov.achievity.data.repository;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UsernameDataSource;

public class UsernameRepository implements UsernameDataSource {

    private static final String USERNAMES = "usernames";

    private final DatabaseReference mDb;

    public UsernameRepository() {
        mDb = FirebaseDatabase.getInstance().getReference().child(USERNAMES);
    }

    @Override
    public void contains(@NonNull String username, @NonNull final DataCallback<Boolean> callback) {
        ValueEventListener usernameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onDataLoaded(dataSnapshot.getValue() != null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataNotAvailable(databaseError.toException());
            }
        };
        mDb.child(username).addListenerForSingleValueEvent(usernameListener);
    }

    @Override
    public void addUsername(@NonNull String username, @NonNull final StatusCallback callback) {
        mDb.child(username).setValue(true)
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
    public void deleteUsername(@NonNull String username, @NonNull final StatusCallback callback) {
        mDb.child(username).removeValue()
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
