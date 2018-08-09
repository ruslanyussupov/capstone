package com.ruslaniusupov.achievity.widget;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.WidgetDataSource;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigureViewModel extends ViewModel {

    private static final String TAG = WidgetConfigureViewModel.class.getSimpleName();

    private AuthDataSource mAuthDataSource;
    private GoalDataSource mGoalDataSource;
    private UserDataDataSource mUserDataDataSource;
    private NoteDataSource mNoteDataSource;
    private WidgetDataSource mWidgetDataSource;
    private MutableLiveData<List<Goal>> mGoalsLiveData;
    private MutableLiveData<List<Note>> mNotesLiveData;

    void init(AuthDataSource authDataSource, GoalDataSource goalDataSource,
              UserDataDataSource userDataDataSource, NoteDataSource noteDataSource,
              WidgetDataSource widgetDataSource) {
        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }
        if (mGoalDataSource == null) {
            mGoalDataSource = goalDataSource;
        }
        if (mUserDataDataSource == null) {
            mUserDataDataSource = userDataDataSource;
        }
        if (mNoteDataSource == null) {
            mNoteDataSource = noteDataSource;
        }
        if (mWidgetDataSource == null) {
            mWidgetDataSource = widgetDataSource;
        }
    }

    private void loadFavGoals() {
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

    private void loadNotes(String goalId) {
        if (mNotesLiveData == null) {
            mNotesLiveData = new MutableLiveData<>();
        }
        mNoteDataSource.getNotes(goalId, new DataCallback<List<Note>>() {
            @Override
            public void onDataLoaded(List<Note> data) {
                Log.d(TAG, "Loaded notes size: " + data.size());
                mNotesLiveData.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load notes.", e);
            }
        });
    }

    LiveData<List<Goal>> getFavGoals() {
        if (mGoalsLiveData == null) {
            loadFavGoals();
        }
        return mGoalsLiveData;
    }

    LiveData<List<Note>> getNotes(String goalId) {
        if (mNotesLiveData == null) {
            loadNotes(goalId);
        }
        return mNotesLiveData;
    }

    void refresh() {
        loadFavGoals();
    }

    WidgetDataSource getWidgetDataSource() {
        return mWidgetDataSource;
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

    public boolean isAuthoried() {
        return mAuthDataSource.isAuthorized();
    }

}
