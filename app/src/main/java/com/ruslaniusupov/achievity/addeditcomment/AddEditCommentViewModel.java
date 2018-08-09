package com.ruslaniusupov.achievity.addeditcomment;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.CommentDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Comment;

public class AddEditCommentViewModel extends ViewModel {

    private static final String TAG = AddEditCommentViewModel.class.getSimpleName();

    private String mNoteId;
    private CommentDataSource mCommentDataSource;
    private AuthDataSource mAuthDataSource;

    public void init(String noteId, CommentDataSource commentDataSource, AuthDataSource authDataSource) {
        if (mNoteId == null) {
            mNoteId = noteId;
        }
        if (mCommentDataSource == null) {
            mCommentDataSource = commentDataSource;
        }
        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }
    }

    public void addComment(String noteId, String commentText) {
        Comment comment = new Comment(mAuthDataSource.getId(), noteId, commentText);
        mCommentDataSource.addComment(comment, new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "The comment added.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add the comment.", e);
            }
        });
    }

    public String getNoteId() {
        return mNoteId;
    }

}
