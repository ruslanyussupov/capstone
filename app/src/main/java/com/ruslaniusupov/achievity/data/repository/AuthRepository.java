package com.ruslaniusupov.achievity.data.repository;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;

public class AuthRepository implements AuthDataSource {

    private final FirebaseAuth mAuth;

    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isAuthorized() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public String getId() {
        return mAuth.getCurrentUser().getUid();
    }

    @Override
    public String getName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    @Override
    public String getEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    @Override
    public void updateName(@NonNull String newName, @NonNull final StatusCallback callback) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
        mAuth.getCurrentUser().updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    @Override
    public void deleteAccount(@NonNull final StatusCallback callback) {
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

}
