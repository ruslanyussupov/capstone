package com.ruslaniusupov.achievity.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WidgetContentProvider extends ContentProvider {

    private static final int NOTE = 100;
    private static final int NOTE_ID = 101;

    private WidgetDbHelper mWidgetDbHelper;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mWidgetDbHelper = new WidgetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mWidgetDbHelper.getReadableDatabase();
        long id;
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case NOTE:
                cursor = db.query(WidgetContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTE_ID:
                id = ContentUris.parseId(uri);
                cursor = db.query(WidgetContract.NoteEntry.TABLE_NAME,
                        projection,
                        WidgetContract.NoteEntry._ID + "=?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTE:
                return WidgetContract.NoteEntry.CONTENT_TYPE;
            case NOTE_ID:
                return WidgetContract.NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mWidgetDbHelper.getWritableDatabase();
        long rowId;
        Uri retUri = null;

        switch (sUriMatcher.match(uri)) {

            case NOTE:
                rowId = db.insert(WidgetContract.NoteEntry.TABLE_NAME, null, values);
                if (rowId > 0) {
                    retUri = WidgetContract.NoteEntry.buildNoteUri(rowId);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }

        if (rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mWidgetDbHelper.getWritableDatabase();
        long id;
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {

            case NOTE:
                rowsDeleted = db.delete(WidgetContract.NoteEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case NOTE_ID:
                id = ContentUris.parseId(uri);
                rowsDeleted = db.delete(WidgetContract.NoteEntry.TABLE_NAME,
                        WidgetContract.NoteEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mWidgetDbHelper.getWritableDatabase();
        long id;
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {

            case NOTE:
                rowsUpdated = db.update(WidgetContract.NoteEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case NOTE_ID:
                id = ContentUris.parseId(uri);
                rowsUpdated = db.update(WidgetContract.NoteEntry.TABLE_NAME,
                        values,
                        WidgetContract.NoteEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);

        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WidgetContract.CONTENT_AUTHORITY, WidgetContract.PATH_NOTE, NOTE);
        matcher.addURI(WidgetContract.CONTENT_AUTHORITY, WidgetContract.PATH_NOTE + "/#",
                NOTE_ID);
        return matcher;
    }

}
