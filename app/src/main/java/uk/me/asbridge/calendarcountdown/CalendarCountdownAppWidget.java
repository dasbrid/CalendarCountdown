package uk.me.asbridge.calendarcountdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CalendarCountdownAppWidgetConfigureActivity CalendarCountdownAppWidgetConfigureActivity}
 */
public class CalendarCountdownAppWidget extends AppWidgetProvider {

    private static final String TAG = LogHelper.makeLogTag(CalendarCountdownAppWidget.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        LogHelper.i(TAG, "onReceive");
        super.onReceive(context, intent);
        String strAction = intent.getAction();

        if (Intent.ACTION_PROVIDER_CHANGED.equals(strAction)) {

            Bundle extras = intent.getExtras();
            LogHelper.i(TAG, "Action=PROVIDER_CHANGED, bundle = ", extras);
            //(extras!=null) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context.getPackageName(), CalendarCountdownAppWidget.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                onUpdate(context, appWidgetManager, appWidgetIds);
            //}
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // from https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
        LogHelper.i(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.collection_widget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widgetListView);
            Intent intent = new Intent(context, MyWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            remoteViews.setRemoteAdapter(R.id.widgetListView, intent);

            Intent configIntent = new Intent(context, CalendarCountdownAppWidgetConfigureActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            //configIntent.setData(Uri.withAppendedPath(Uri.parse("abc" + "://widget/id/"), String.valueOf(appWidgetId)));
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewSettings, configPendingIntent);


            // Register an onClickListener for the update (refresh)
            Intent updateIntent = new Intent(context, CalendarCountdownAppWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewUpdate, pendingIntent);


            remoteViews.setTextViewText(R.id.widgetTitleLabel, Configuration.getTitlePref(context, appWidgetId)); //Integer.toString(appWidgetId) );

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            Configuration.deletePrefs(context, appWidgetId);
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

