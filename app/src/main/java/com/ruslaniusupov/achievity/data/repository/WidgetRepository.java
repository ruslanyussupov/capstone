package com.ruslaniusupov.achievity.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruslaniusupov.achievity.data.WidgetDataSource;
import com.ruslaniusupov.achievity.data.db.WidgetContract;
import com.ruslaniusupov.achievity.data.model.Note;

import java.util.ArrayList;
import java.util.List;

public class WidgetRepository implements WidgetDataSource {

    private final Context mContext;

    public WidgetRepository(Context context) {
        mContext = context;
    }

    @Override
    public void addNotes(int widgetId, List<Note> notes) {

        for (Note note : notes) {
            ContentValues cv = new ContentValues();
            cv.put(WidgetContract.NoteEntry.COLUMN_WIDGET_ID, widgetId);
            cv.put(WidgetContract.NoteEntry.COLUMN_NOTE_ID, note.getId());
            cv.put(WidgetContract.NoteEntry.COLUMN_AUTHOR_ID, note.getAuthorId());
            cv.put(WidgetContract.NoteEntry.COLUMN_GOAL_ID, note.getGoalId());
            cv.put(WidgetContract.NoteEntry.COLUMN_AUTHOR, note.getAuthor());
            cv.put(WidgetContract.NoteEntry.COLUMN_TEXT, note.getText());
            cv.put(WidgetContract.NoteEntry.COLUMN_TIMESTAMP, note.getTimestamp());

            mContext.getContentResolver().insert(WidgetContract.NoteEntry.CONTENT_URI, cv);
        }

    }

    @Override
    public List<Note> getNotes(int widgetId) {

        List<Note> notes = null;

        Cursor cursor = mContext.getContentResolver().query(WidgetContract.NoteEntry.CONTENT_URI,
                null,
                WidgetContract.NoteEntry.COLUMN_WIDGET_ID + "=?",
                new String[]{String.valueOf(widgetId)},
                null);

        if (cursor != null) {

            try {
                if (cursor.getCount() != 0) {
                    notes = new ArrayList<>(cursor.getCount());

                    while (cursor.moveToNext()) {
                        notes.add(new Note(
                                cursor.getString(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_NOTE_ID)),
                                cursor.getString(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_AUTHOR_ID)),
                                cursor.getString(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_GOAL_ID)),
                                cursor.getString(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_AUTHOR)),
                                cursor.getString(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_TEXT)),
                                cursor.getLong(cursor.getColumnIndex(WidgetContract.NoteEntry.COLUMN_TIMESTAMP))
                        ));
                    }
                }
            } finally {
                cursor.close();
            }

        }

        return notes;

    }

    @Override
    public void deleteNotes(int widgetId) {
        mContext.getContentResolver().delete(WidgetContract.NoteEntry.CONTENT_URI,
                WidgetContract.NoteEntry.COLUMN_WIDGET_ID + "=?",
                new String[]{String.valueOf(widgetId)});
    }

}
