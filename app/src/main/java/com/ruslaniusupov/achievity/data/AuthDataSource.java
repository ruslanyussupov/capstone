package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

public interface AuthDataSource {

    boolean isAuthorized();

    String getId();

    String getName();

    String getEmail();

    void updateName(@NonNull String newName, @NonNull StatusCallback callback);

    void deleteAccount(@NonNull StatusCallback callback);

}
