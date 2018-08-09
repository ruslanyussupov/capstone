package com.ruslaniusupov.achievity.adapters;

import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.CommentDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.UserDataDataSource;
import com.ruslaniusupov.achievity.data.model.Comment;
import com.ruslaniusupov.achievity.data.model.User;
import com.ruslaniusupov.achievity.utils.Utils;

class CommentItemPresenter implements CommentItemContract.Presenter {

    private static final String TAG = CommentItemPresenter.class.getSimpleName();

    private final CommentItemContract.View mView;
    private final UserDataDataSource mUserDataDataSource;
    private final CommentDataSource mCommentDataSource;
    private final AuthDataSource mAuthDataSource;

    CommentItemPresenter(CommentItemContract.View view, UserDataDataSource userDataDataSource,
                         CommentDataSource commentDataSource, AuthDataSource authDataSource) {
        mView = view;
        mUserDataDataSource = userDataDataSource;
        mCommentDataSource = commentDataSource;
        mAuthDataSource = authDataSource;
    }

    @Override
    public void updateUi(Comment comment) {
        initBtnState(comment.getNoteId(), comment.getId());
        mUserDataDataSource.getName(comment.getAuthorId(), new DataCallback<String>() {
            @Override
            public void onDataLoaded(String name) {
                mView.showAuthor(name);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load user's data.", e);
            }
        });
        updateLikesCount(comment.getNoteId(), comment.getId());
        mView.showText(comment.getText());
        mView.showPublishDate(Utils.formatTimestamp(comment.getTimestamp()));

    }

    private void initBtnState(String noteId, String commentId) {
        mCommentDataSource.isLiked(noteId, commentId, mAuthDataSource.getId(), new DataCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean data) {
                mView.setFavBtnState(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load user's liked list.", e);
            }
        });
    }

    @Override
    public void like(final String noteId, final String commentId) {
        mCommentDataSource.addUserLiked(noteId, commentId, mAuthDataSource.getId(), new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "The like added.");
                updateLikesCount(noteId, commentId);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add the like.", e);
            }
        });
    }

    @Override
    public void cancelLike(final String noteId, final String commentId) {
        mCommentDataSource.deleteUserLiked(noteId, commentId, mAuthDataSource.getId(), new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "The like removed.");
                updateLikesCount(noteId, commentId);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't remove the like.", e);
            }
        });
    }

    private void updateLikesCount(String noteId, String commentId) {
        mCommentDataSource.getLikesCount(noteId, commentId, new DataCallback<Long>() {
            @Override
            public void onDataLoaded(Long data) {
                if (data == null) {
                    return;
                }
                mView.showLikeCount(String.valueOf(data));
            }

            @Override
            public void onDataNotAvailable(Exception e) {

            }
        });
    }
}
