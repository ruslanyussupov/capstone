package com.ruslaniusupov.achievity.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.WidgetDataSource;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.repository.WidgetRepository;
import com.ruslaniusupov.achievity.utils.Utils;

import java.util.List;

public class AppWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(getPackageName(), intent,
                new WidgetRepository(getApplicationContext()));
    }

    class NoteRemoteViewsFactory implements RemoteViewsFactory {

        private final String TAG = NoteRemoteViewsFactory.class.getSimpleName();

        private final int mWidgetId;
        private final String mPackageName;
        private final WidgetDataSource mWidgetDataSource;
        private List<Note> mNotes;

        NoteRemoteViewsFactory(String packageName, Intent intent, WidgetDataSource widgetDataSource) {
            mPackageName = packageName;
            mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            mWidgetDataSource = widgetDataSource;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mNotes = mWidgetDataSource.getNotes(mWidgetId);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mNotes == null ? 0 : mNotes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mPackageName, R.layout.item_widget);

            Note note = mNotes.get(position);

            views.setTextViewText(R.id.author, note.getAuthor());
            views.setTextViewText(R.id.publish_date, Utils.formatTimestamp(note.getTimestamp()));
            views.setTextViewText(R.id.body, note.getText());

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
