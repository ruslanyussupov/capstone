package com.ruslaniusupov.achievity.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.Goal;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();

    private MutableLiveData<List<Goal>> mGoalsLiveData;
    private GoalDataSource mGoalDataSource;
    private AuthDataSource mAuthDataSource;
    private UserDataDataSource mUserDataDataSource;

    public void init(AuthDataSource authDataSource, UserDataDataSource userDataDataSource,
                     GoalDataSource goalDataSource) {
        if (mGoalDataSource == null) {
            mGoalDataSource = goalDataSource;
        }
        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }
        if (mUserDataDataSource == null) {
            mUserDataDataSource = userDataDataSource;
        }
    }

    private void loadGoals() {
        if (mGoalsLiveData == null) {
            mGoalsLiveData = new MutableLiveData<>();
        }
        mUserDataDataSource.getSubscriptions(mAuthDataSource.getId(), new DataCallback<List<String>>() {
            @Override
            public void onDataLoaded(List<String> subscriptions) {
                if (subscriptions == null || subscriptions.size() == 0) {
                    mGoalsLiveData.setValue(null);
                    return;
                }
                GoalsCallback callback = new GoalsCallback(subscriptions.size());
                for (String goalId : subscriptions) {
                    mGoalDataSource.getGoal(goalId, callback);
                }
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load subscriptions.", e);
            }
        });
    }

    public LiveData<List<Goal>> getGoals() {
        if (mGoalsLiveData == null) {
            Log.d(TAG, "Goals live data is null.");
            loadGoals();
        }
        return mGoalsLiveData;
    }

    public void refresh() {
        loadGoals();
    }

    private class GoalsCallback implements DataCallback<Goal> {

        private int mGoalsCount;
        private final ArrayList<Goal> mGoals;

        GoalsCallback(int goalsCount) {
            mGoalsCount = goalsCount;
            mGoals = new ArrayList<>();
        }

        @Override
        public void onDataLoaded(Goal goal) {
            mGoalsCount--;
            mGoals.add(goal);
            if (mGoalsCount == 0) {
                mGoalsLiveData.setValue(mGoals);
            }
        }

        @Override
        public void onDataNotAvailable(Exception e) {
            Log.e(TAG, "Can't load a goal.", e);
        }
    }

}
