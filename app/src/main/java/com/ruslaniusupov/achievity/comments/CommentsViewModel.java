package com.ruslaniusupov.achievity.comments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.CommentDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.model.Comment;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private static final String TAG = CommentsViewModel.class.getSimpleName();

    private CommentDataSource mCommentDataSource;
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private String mNoteId;

    public void init(String noteId, CommentDataSource commentDataSource) {
        if (mCommentDataSource == null) {
            mCommentDataSource = commentDataSource;
        }
        if (mNoteId == null) {
            mNoteId = noteId;
        }
    }

    private void loadComments() {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
        }
        mCommentDataSource.getComments(mNoteId, new DataCallback<List<Comment>>() {
            @Override
            public void onDataLoaded(List<Comment> data) {
                mCommentsLiveData.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load comments.", e);
            }
        });
    }

    public LiveData<List<Comment>> getComments() {
        if (mCommentsLiveData == null) {
            loadComments();
        }
        return mCommentsLiveData;
    }

    public void refresh() {
        loadComments();
    }

    public String getNoteId() {
        return mNoteId;
    }

}
