package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.User;


public class EditProfileViewModel extends ViewModel {

    private static final String TAG = EditProfileViewModel.class.getSimpleName();

    private String mUserId;
    private UserDataDataSource mUserData;
    private MutableLiveData<User> mUser;
    private String mOldName;
    private String mOldBio;

    public void init(String userId, UserDataDataSource userDataDataSource) {
        if (mUserId == null) {
            mUserId = userId;
        }
        if (mUserData == null) {
            mUserData = userDataDataSource;
        }
    }

    public void updateName(final String newName) {
        mUserData.updateName(mUserId, newName, new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Name updated to " + newName);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't update the name: " + newName, e);
            }
        });
    }

    public void updateBio(final String newBio) {
        mUserData.updateBio(mUserId, newBio, new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Bio updated to " + newBio);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't update the bio: " + newBio, e);
            }
        });
    }

    public LiveData<User> getUser() {
        if (mUser == null) {
            mUser = new MutableLiveData<>();
            mUserData.getUser(mUserId, new DataCallback<User>() {
                @Override
                public void onDataLoaded(User data) {
                    mOldName = data.getName();
                    mOldBio = data.getBio();
                    mUser.setValue(data);
                }

                @Override
                public void onDataNotAvailable(Exception e) {
                    Log.e(TAG, "Can't load user: " + mUserId, e);
                }
            });
        }
        return mUser;
    }

    public String getOldName() {
        return mOldName;
    }

    public String getOldBio() {
        return mOldBio;
    }
}
