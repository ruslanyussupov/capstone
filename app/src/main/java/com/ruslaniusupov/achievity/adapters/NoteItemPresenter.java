package com.ruslaniusupov.achievity.adapters;

import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.adapters.NoteItemContract.View;
import com.ruslaniusupov.achievity.adapters.NoteItemContract.Presenter;
import com.ruslaniusupov.achievity.utils.Utils;

class NoteItemPresenter implements Presenter {

    private static final String TAG = NoteItemPresenter.class.getSimpleName();

    private final View mView;
    private final AuthDataSource mAuthDataSource;
    private final NoteDataSource mNoteDataSource;

    NoteItemPresenter(View view, AuthDataSource authDataSource, NoteDataSource noteDataSource) {
        mView = view;
        mAuthDataSource = authDataSource;
        mNoteDataSource = noteDataSource;
    }

    @Override
    public void updateUi(Note note) {
        initButtonsState(note.getId());
        updateLikesCount(note.getId());
        mNoteDataSource.getCommentsCount(note.getId(), new DataCallback<Long>() {
            @Override
            public void onDataLoaded(Long data) {
                if (data == null) {
                    return;
                }
                mView.showCommentsCount(String.valueOf(data));
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't get comments count.", e);
            }
        });
        mView.showAuthor(note.getAuthor());
        mView.showPubDate(Utils.formatTimestamp(note.getTimestamp()));
        mView.showText(note.getText());
    }

    @Override
    public void like(final String noteId) {
        mNoteDataSource.addUserLiked(noteId, mAuthDataSource.getId(), new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Like added.");
                updateLikesCount(noteId);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add the like.", e);
            }
        });
    }

    @Override
    public void cancelLike(final String noteId) {
        mNoteDataSource.deleteUserLiked(noteId, mAuthDataSource.getId(), new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Like removed.");
                updateLikesCount(noteId);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't remove the like.", e);
            }
        });
    }

    private void initButtonsState(String noteId) {
        mNoteDataSource.isLiked(noteId, mAuthDataSource.getId(), new DataCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean data) {
                mView.setLikeBtnState(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load like state.", e);
            }
        });
    }

    private void updateLikesCount(String noteId) {
        mNoteDataSource.getLikesCount(noteId, new DataCallback<Long>() {
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
}
