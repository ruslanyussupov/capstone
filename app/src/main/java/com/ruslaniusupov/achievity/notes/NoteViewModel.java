package com.ruslaniusupov.achievity.notes;

import android.arch.lifecycle.ViewModel;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.utils.Utils;

public class NoteViewModel extends ViewModel {

    private static final String TAG = NoteViewModel.class.getSimpleName();

    private Note mNote;

    public void init(Note note) {
        if (mNote == null) {
            mNote = note;
        }
    }

    public String getText() {
        return mNote.getText();
    }

    public String getPublishDate() {
        return Utils.formatTimestamp(mNote.getTimestamp());
    }

    public String getAuthor() {
        return mNote.getAuthor();
    }

    public String getNoteId() {
        return mNote.getId();
    }

}
