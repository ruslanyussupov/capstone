package com.ruslaniusupov.achievity.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WidgetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "widget.db";
    private static final int DATABASE_VERSION = 1;

    public WidgetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_NOTES_TABLE = "CREATE TABLE " + WidgetContract.NoteEntry.TABLE_NAME + " ("
                + WidgetContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WidgetContract.NoteEntry.COLUMN_WIDGET_ID + " INTEGER NOT NULL, "
                + WidgetContract.NoteEntry.COLUMN_NOTE_ID + " TEXT NOT NULL, "
                + WidgetContract.NoteEntry.COLUMN_AUTHOR_ID + " TEXT NOT NULL, "
                + WidgetContract.NoteEntry.COLUMN_GOAL_ID + " TEXT NOT NULL, "
                + WidgetContract.NoteEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + WidgetContract.NoteEntry.COLUMN_TEXT + " TEXT, "
                + WidgetContract.NoteEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL);";

        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
