package com.ruslaniusupov.achievity.data.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WidgetContract {

    public static final String CONTENT_AUTHORITY = "com.ruslaniusupov.achievity";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTE = "note";

    public static final class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_WIDGET_ID = "widgetId";
        public static final String COLUMN_NOTE_ID = "noteId";
        public static final String COLUMN_AUTHOR_ID = "authorId";
        public static final String COLUMN_GOAL_ID = "goalId";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static Uri buildNoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
