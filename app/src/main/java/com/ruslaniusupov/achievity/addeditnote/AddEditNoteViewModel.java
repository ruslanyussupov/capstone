package com.ruslaniusupov.achievity.addeditnote;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ruslaniusupov.achievity.data.AuthDataSource;
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.StatusCallback;
import com.ruslaniusupov.achievity.data.model.Note;

public class AddEditNoteViewModel extends ViewModel {

    private static final String TAG = AddEditNoteViewModel.class.getSimpleName();

    private NoteDataSource mNoteDataSource;
    private AuthDataSource mAuthDataSource;
    private String mGoalId;

    public void init(String goalId, NoteDataSource noteDataSource, AuthDataSource authDataSource) {
        if (mGoalId == null) {
            mGoalId = goalId;
        }
        if (mNoteDataSource == null) {
            mNoteDataSource = noteDataSource;
        }
        if (mAuthDataSource == null) {
            mAuthDataSource = authDataSource;
        }
    }

    public void addNote(String noteText) {
        Note note = new Note(mAuthDataSource.getId(), mGoalId, mAuthDataSource.getName(), noteText);
        mNoteDataSource.addNote(note, new StatusCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Note added.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Can't add the note.", e);
            }
        });
    }

}
