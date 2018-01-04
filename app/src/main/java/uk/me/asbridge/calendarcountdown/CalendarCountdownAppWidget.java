package uk.me.asbridge.calendarcountdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CalendarCountdownAppWidgetConfigureActivity CalendarCountdownAppWidgetConfigureActivity}
 */
public class CalendarCountdownAppWidget extends AppWidgetProvider {

    private static final String TAG = LogHelper.makeLogTag(CalendarCountdownAppWidget.class);
    public static final String ITEM_CLICK_ACTION = "uk.me.asbridge.calendarcountdown.ITEM_CLICK_ACTION";
    public static final String EXTRA_ITEM_LIST_POSTION = "com.example.android.stackwidget.EXTRA_ITEM_LIST_POSTION";
    public static final String EXTRA_ITEM_EVENT_ID = "com.example.android.stackwidget.EXTRA_ITEM_EVENT_ID";
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String strAction = intent.getAction();
        LogHelper.i(TAG, "onReceive: action=", strAction);

        // This action intent is broadcast when an item is clicked (this is set up in the "onUpdate" method of this class)
        // The Factory adds the EXTRA_ITEM_LIST_POSTION information specific to each item
        if (strAction.equals(ITEM_CLICK_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM_LIST_POSTION, 0);
            long calendarEventId = intent.getLongExtra(EXTRA_ITEM_EVENT_ID, -1);
            LogHelper.i(TAG, "Touched view " ,viewIndex , " event id=" , calendarEventId);
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calendarEventId);
            Intent EditCalendarIntent = new Intent(Intent.ACTION_VIEW)
                    .setData(uri)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(EditCalendarIntent);
        }

        if (Intent.ACTION_PROVIDER_CHANGED.equals(strAction)) {

            Bundle extras = intent.getExtras();
            LogHelper.i(TAG, "Action=PROVIDER_CHANGED, bundle = ", extras);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);


            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), CalendarCountdownAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);

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
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter( R.id.widgetListView, intent);

            // clickIntent for the settings icon (launch configuration class)
            Intent configIntent = new Intent(context, CalendarCountdownAppWidgetConfigureActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            configIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewSettings, configPendingIntent);

            // Register an onClickListener for the update (refresh)
            Intent updateIntent = new Intent(context, CalendarCountdownAppWidget.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.imageViewUpdate, pendingIntent);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent toastIntent = new Intent(context, CalendarCountdownAppWidget.class);
            // Set the action for the intent.
            // When the user touches a particular view, it will have the effect of
            // broadcasting ITEM_CLICK_ACTION.
            toastIntent.setAction(CalendarCountdownAppWidget.ITEM_CLICK_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widgetListView, toastPendingIntent);


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

