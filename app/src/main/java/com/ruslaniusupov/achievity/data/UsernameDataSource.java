package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

public interface UsernameDataSource {

    void contains(@NonNull String username, @NonNull DataCallback<Boolean> callback);

    void addUsername(@NonNull String username, @NonNull StatusCallback callback);

    void deleteUsername(@NonNull String username, @NonNull StatusCallback callback);

}
