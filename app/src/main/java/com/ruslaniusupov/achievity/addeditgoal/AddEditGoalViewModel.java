package com.ruslaniusupov.achievity.addeditgoal;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Goal;

public class AddEditGoalViewModel extends ViewModel {

    private static final String TAG = AddEditGoalActivity.class.getSimpleName();

    private String mUserId;
    private GoalDataSource mGoalDataSource;

    public void init(String userId, GoalDataSource goalDataSource) {
        if (mUserId == null) {
            mUserId = userId;
        }
        if (mGoalDataSource == null) {
            mGoalDataSource = goalDataSource;
        }
    }

    public void addGoal(final String goalText) {

        mGoalDataSource.addGoal(new Goal(mUserId, goalText), new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Goal added. " + goalText);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add the goal.", e);
            }
        });

    }

}
