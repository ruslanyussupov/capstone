package com.ruslaniusupov.achievity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String formatTimestamp(long timestamp) {

        Date date = new Date(timestamp);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();

        return dateFormat.format(date);

    }

}
