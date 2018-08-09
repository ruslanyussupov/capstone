package com.ruslaniusupov.achievity.adapters;

import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.GoalDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.Goal;
import com.ruslaniusupov.achievity.utils.Utils;


class GoalItemPresenter implements GoalItemContract.Presenter {

    private static final String TAG = GoalItemPresenter.class.getSimpleName();

    private final GoalItemContract.View mView;
    private final UserDataDataSource mUserDataRepository;
    private final GoalDataSource mGoalsRepository;
    private final AuthDataSource mAuthRepository;

    GoalItemPresenter(GoalItemContract.View view, UserDataDataSource userDataDataSource,
                      GoalDataSource goalDataSource, AuthDataSource authDataSource) {
        mView = view;
        mUserDataRepository = userDataDataSource;
        mGoalsRepository = goalDataSource;
        mAuthRepository = authDataSource;
    }

    @Override
    public void updateUi(Goal goal) {
        initButtonsState(goal.getId());
        mUserDataRepository.getName(goal.getAuthorId(), new DataCallback<String>() {
            @Override
            public void onDataLoaded(String name) {
                mView.showAuthor(name);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load user's data.", e);
            }
        });
        updateLikesCount(goal.getId());
        updateSubscribesCount(goal.getId());
        mGoalsRepository.getNotesCount(goal.getId(), new DataCallback<Long>() {
            @Override
            public void onDataLoaded(Long data) {
                if (data == null) {
                    return;
                }
                mView.showNotesCount(String.valueOf(data));
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load notes count.", e);
            }
        });
        mView.showPubDate(Utils.formatTimestamp(goal.getTimestamp()));
        mView.showText(goal.getText());
    }

    private void initButtonsState(String goalId) {
        final String currentUserId = mAuthRepository.getId();
        mGoalsRepository.isLiked(goalId, currentUserId, new DataCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean data) {
                mView.setLikeBtnState(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get user liked");
            }
        });
        mGoalsRepository.isSubscribed(goalId, currentUserId, new DataCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean data) {
                mView.setSubscribeBtnState(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get user subscribed");
            }
        });
    }

    @Override
    public void like(final String goalId) {
        final String currentUserId = mAuthRepository.getId();
        mGoalsRepository.addUserLiked(goalId, currentUserId, new StatusCallback() {
            @Override
            public void onSuccess() {
                updateLikesCount(goalId);
                Log.d(TAG, "User " + currentUserId +  " added to the goal's users liked list");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add user to the goal's users liked list", e);
            }
        });
    }

    @Override
    public void cancelLike(final String goalId) {
        final String currentUserId = mAuthRepository.getId();
        mGoalsRepository.deleteUserLiked(goalId, currentUserId, new StatusCallback() {
            @Override
            public void onSuccess() {
                updateLikesCount(goalId);
                Log.d(TAG, "User " + currentUserId + " removed from goal's user liked list.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't remove user from the goal's users liked list", e);
            }
        });
    }

    @Override
    public void subscribe(final String goalId) {
        final String currentUserId = mAuthRepository.getId();
        mGoalsRepository.addSubscriber(goalId, currentUserId, new StatusCallback() {
            @Override
            public void onSuccess() {
                updateSubscribesCount(goalId);
                Log.d(TAG, "Subscriber " + currentUserId + " added to the goal's subscribers list.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add subscriber.", e);
            }
        });
    }

    @Override
    public void unsubscribe(final String goalId) {
        final String currentUserId = mAuthRepository.getId();
        mGoalsRepository.deleteSubscriber(goalId, currentUserId, new StatusCallback() {
            @Override
            public void onSuccess() {
                updateSubscribesCount(goalId);
                Log.d(TAG, "Subscriber " + currentUserId + " removed from the goal's subscribers list.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't remove subscriber.", e);
            }
        });
    }

    private void updateLikesCount(String goalId) {
        mGoalsRepository.getLikesCount(goalId, new DataCallback<Long>() {
            @Override
            public void onDataLoaded(Long data) {
                if (data == null) {
                    return;
                }
                mView.showLikesCount(String.valueOf(data));
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load likes count.", e);
            }
        });
    }

    private void updateSubscribesCount(String goalId) {
        mGoalsRepository.getSubscribersCount(goalId, new DataCallback<Long>() {
            @Override
            public void onDataLoaded(Long data) {
                if (data == null) {
                    return;
                }
                mView.showSubscribersCount(String.valueOf(data));
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load subscribers count.", e);
            }
        });
    }
}
