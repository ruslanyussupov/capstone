package com.ruslaniusupov.achievity.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.model.User;

import java.util.List;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = ProfileViewModel.class.getSimpleName();

    private String mUserId;
    private String mCurrentUserId;
    private AuthDataSource mAuthDataSource;
    private GoalDataSource mGoalDataSource;
    private UserDataDataSource mUserDataDataSource;
    private MutableLiveData<List<Goal>> mGoals;
    private MutableLiveData<User> mUser;

    public void init(String userId, AuthDataSource authDataSource, GoalDataSource goalDataSource,
                     UserDataDataSource userDataDataSource) {

        mUserId = userId;
        mAuthDataSource = authDataSource;
        mGoalDataSource = goalDataSource;
        mUserDataDataSource = userDataDataSource;

        if (mUser == null) {
            mUser = new MutableLiveData<>();
            loadUserData();
        }

        if (mGoals == null) {
            mGoals = new MutableLiveData<>();
            loadGoals();
        }

        if (mCurrentUserId == null) {
            mCurrentUserId = mAuthDataSource.getId();
        }

    }

    private void loadUserData() {
        mUserDataDataSource.getUser(mUserId, new DataCallback<User>() {
            @Override
            public void onDataLoaded(User data) {
                mUser.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load user's data.", e);
            }
        });
    }

    private void loadGoals() {
        mGoalDataSource.getGoals(mUserId, new DataCallback<List<Goal>>() {
            @Override
            public void onDataLoaded(List<Goal> data) {
                mGoals.setValue(data);
            }
            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load goals.", e);
            }
        });
    }

    public LiveData<User> getUser() {
        return mUser;
    }

    public LiveData<List<Goal>> getGoals() {
        return mGoals;
    }

    public void refresh() {
        loadUserData();
        loadGoals();
    }

    public AuthDataSource getAuthDataSource() {
        return mAuthDataSource;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getCurrentUserId() {
        return mCurrentUserId;
    }

}
