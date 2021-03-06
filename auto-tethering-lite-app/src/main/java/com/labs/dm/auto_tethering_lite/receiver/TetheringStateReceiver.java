package com.labs.dm.auto_tethering_lite.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.labs.dm.auto_tethering_lite.R;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;

/**
 * Created by Daniel Mroczka
 */
public class TetheringStateReceiver extends BroadcastReceiver {

    private final String TAG = "TetheringStateChange";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.getAction());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, TetheringWidgetProvider.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            Log.i(TAG, "widget id=" + widgetId);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), getLayout(intent));
            Intent widgetIntent = new Intent(context, TetheringWidgetProvider.class);
            widgetIntent.putExtra(EXTRA_APPWIDGET_ID, widgetId);
            widgetIntent.setAction("widget.click");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, widgetIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            context.getSharedPreferences("widget", 0).edit().putInt("clicks", 0).apply();
        } else {
            context.getSharedPreferences("widget", 0).edit().putInt("clicks", 0).commit();
        }
    }

    private int getLayout(Intent intent) {
        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

        int layout = R.layout.widget_layout_wait;
        switch (state % 10) {
            case WIFI_STATE_ENABLED:
                layout = R.layout.widget_layout_on;
                break;
            case WIFI_STATE_DISABLED:
                layout = R.layout.widget_layout_off;
                break;
        }
        return layout;
    }
}
