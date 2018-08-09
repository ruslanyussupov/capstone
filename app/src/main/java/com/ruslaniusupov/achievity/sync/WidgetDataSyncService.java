package com.ruslaniusupov.achievity.sync;

import android.appwidget.AppWidgetManager;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.ruslaniusupov.achievity.data.DataCallback;
import com.ruslaniusupov.achievity.data.NoteDataSource;
import com.ruslaniusupov.achievity.data.WidgetDataSource;
import com.ruslaniusupov.achievity.data.model.Note;
import com.ruslaniusupov.achievity.data.repository.NoteRepository;
import com.ruslaniusupov.achievity.data.repository.WidgetRepository;

import java.util.List;

public class WidgetDataSyncService extends JobService {

    private static final String TAG = WidgetDataSyncService.class.getSimpleName();
    public static final String EXTRA_GOAL_ID = "goal_id";
    public static final String EXTRA_WIDGET_ID = "widget_id";

    private final NoteDataSource mNoteDataSource = new NoteRepository();
    private final WidgetDataSource mWidgetDataSource = new WidgetRepository(this);

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "onStartJob");
        if (job.getExtras() == null) {
            return false;
        }

        String goalId = job.getExtras().getString(EXTRA_GOAL_ID);
        final int widgetId = job.getExtras().getInt(EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (TextUtils.isEmpty(goalId) || widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return false;
        }

        mNoteDataSource.getNotes(goalId, new DataCallback<List<Note>>() {
            @Override
            public void onDataLoaded(List<Note> data) {
                mWidgetDataSource.deleteNotes(widgetId);
                mWidgetDataSource.addNotes(widgetId, data);
                Log.d(TAG, "Widget data synchronized.");
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                Log.e(TAG, "Can't load notes.", e);
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob");
        return false;
    }
}
