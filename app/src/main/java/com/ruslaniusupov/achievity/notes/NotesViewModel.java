package com.ruslaniusupov.achievity.notes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.model.Note;

import java.util.List;

public class NotesViewModel extends ViewModel {

    private static final String TAG = NotesViewModel.class.getSimpleName();

    private String mGoalId;
    private String mAuthorId;
    private NoteDataSource mNoteDataSource;
    private AuthDataSource mAuthDataSource;
    private MutableLiveData<List<Note>> mNotesLiveData;

    public void init(String goalId, String authorId, NoteDataSource noteDataSource, AuthDataSource authDataSource) {
        if (mGoalId == null) {
            mGoalId = goalId;
        }
        if (mAuthorId == null) {
            mAuthorId = authorId;
        }
        if (mNoteDataSource == null) {
            mNoteDataSource = noteDataSource;
        }
        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }
    }

    private void loadNotes() {
        if (mNotesLiveData == null) {
            mNotesLiveData = new MutableLiveData<>();
        }
        mNoteDataSource.getNotes(mGoalId, new DataCallback<List<Note>>() {
            @Override
            public void onDataLoaded(List<Note> data) {
                Log.d(TAG, "Notes size: " + data.size());
                mNotesLiveData.setValue(data);
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load notes.", e);
            }
        });
    }

    public LiveData<List<Note>> getNotes() {
        if (mNotesLiveData == null) {
            loadNotes();
        }
        return mNotesLiveData;
    }

    public String getCurrentUserId() {
        return mAuthDataSource.getId();
    }

    public String getGoalId() {
        return mGoalId;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

}
