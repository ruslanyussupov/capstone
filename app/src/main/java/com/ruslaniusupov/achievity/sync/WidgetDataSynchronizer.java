package com.ruslaniusupov.achievity.sync;

import android.content.Context;
import android.os.Bundle;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class WidgetDataSynchronizer {

    public static void scheduleSync(Context context, String goalId, int widgetId) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Bundle extras = new Bundle();
        extras.putString(WidgetDataSyncService.EXTRA_GOAL_ID, goalId);
        extras.putInt(WidgetDataSyncService.EXTRA_WIDGET_ID, widgetId);
        Job sync = dispatcher.newJobBuilder()
                .setService(WidgetDataSyncService.class)
                .setTag(String.valueOf(widgetId))
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setExtras(extras)
                .setTrigger(Trigger.executionWindow(3600, 7200))
                .setConstraints(Constraint.ON_UNMETERED_NETWORK, Constraint.DEVICE_IDLE)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        dispatcher.mustSchedule(sync);
    }

    public static void cancelSync(Context context, int widgetId) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(String.valueOf(widgetId));
    }

}
