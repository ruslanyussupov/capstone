package com.ruslaniusupov.achievity;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.User;

public class MainViewModel extends ViewModel{

    private static final String TAG = MainViewModel.class.getSimpleName();

    private AuthDataSource mAuthDataSource;
    private UserDataDataSource mUserDataDataSource;

    public void init(AuthDataSource authDataSource, UserDataDataSource userDataDataSource) {

        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }

        if (mUserDataDataSource == null) {
            mUserDataDataSource = userDataDataSource;
        }

    }

    public boolean isAuthorized() {
        return mAuthDataSource.isAuthorized();
    }

    public String getCurrentUserId() {
        return mAuthDataSource.getId();
    }

    public void initUserData() {
        mUserDataDataSource.getUser(mAuthDataSource.getId(), new DataCallback<User>() {
            @Override
            public void onDataLoaded(User data) {
                if (data == null) {
                    final User user = new User(mAuthDataSource.getId(), mAuthDataSource.getName());
                    mUserDataDataSource.addUser(user, new StatusCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "User data updated: " + user.getId());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Can't add user", e);
                        }
                    });
                }
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get user's data.", e);
            }
        });
    }

}
