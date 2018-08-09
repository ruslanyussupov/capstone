package com.ruslaniusupov.achievity.discover;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.model.Goal;

import java.util.List;

public class DiscoverViewModel extends ViewModel {

    private static final String TAG = DiscoverViewModel.class.getSimpleName();

    private GoalDataSource mGoalDataSource;
    private MutableLiveData<List<Goal>> mGoalsLiveData;
    private MutableLiveData<List<Goal>> mMostPopularGoalsLiveData;
    private Sort mSort = Sort.POPULAR;


    public void init(GoalDataSource goalDataSource) {
        if (mGoalDataSource == null) {
            mGoalDataSource = goalDataSource;
        }
    }

    private void loadRecentGoals() {
        if (mGoalsLiveData == null) {
            Log.d(TAG, "Goals live data is null.");
            mGoalsLiveData = new MutableLiveData<>();
        }
        mGoalDataSource.getRecentGoals(new DataCallback<List<Goal>>() {
            @Override
            public void onDataLoaded(List<Goal> data) {
                mGoalsLiveData.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load recent goals.", e);
            }
        });
    }

    private void loadMostPopularGoals() {
        if (mMostPopularGoalsLiveData == null) {
            Log.d(TAG, "Goals live data is null.");
            mMostPopularGoalsLiveData = new MutableLiveData<>();
        }
        mGoalDataSource.getMostLikedGoals(new DataCallback<List<Goal>>() {
            @Override
            public void onDataLoaded(List<Goal> data) {
                mMostPopularGoalsLiveData.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load most popular goals.", e);
            }
        });
    }

    public LiveData<List<Goal>> getGoals() {
        if (mSort == Sort.RECENT) {
            if (mGoalsLiveData == null) {
                loadRecentGoals();
            }
            Log.d(TAG, "Get recent goals LiveData.");
            return mGoalsLiveData;
        } else {
            if (mMostPopularGoalsLiveData == null) {
                loadMostPopularGoals();
            }
            Log.d(TAG, "Get popular goals LiveData.");
            return mMostPopularGoalsLiveData;
        }
    }

    public void refreshGoals() {
        switch (mSort) {
            case RECENT:
                loadRecentGoals();
                break;
            case POPULAR:
                loadMostPopularGoals();
                break;
        }
    }

    public void setSort(Sort sort) {
        mSort = sort;
        refreshGoals();
    }

    public Sort getSort() {
        return mSort;
    }

    enum Sort {
        RECENT,
        POPULAR
    }

}
