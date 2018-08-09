package com.ruslaniusupov.achievity.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.ruslaniusupov.achievity.R;
import com.ruslaniusupov.achievity.data.WidgetDataSource;
import com.ruslaniusupov.achievity.data.repository.WidgetRepository;
import com.ruslaniusupov.achievity.sync.WidgetDataSynchronizer;

// Shows notes of a specific goal user subscribed on.
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fav_goal_widget);

        Intent widgetService = new Intent(context, AppWidgetService.class);
        widgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetService.setData(Uri.parse(widgetService.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.notes_lv, widgetService);
        views.setEmptyView(R.id.notes_lv, R.id.emptyView);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        WidgetDataSource widgetDataSource = new WidgetRepository(context);
        for (int appWidgetId : appWidgetIds) {
            widgetDataSource.deleteNotes(appWidgetId);
            WidgetDataSynchronizer.cancelSync(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

