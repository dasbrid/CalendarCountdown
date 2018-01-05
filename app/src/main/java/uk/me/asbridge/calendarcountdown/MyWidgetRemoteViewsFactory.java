package uk.me.asbridge.calendarcountdown;

/**
 * Created by AsbridgeD on 19-Dec-17.
 */

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */
public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = LogHelper.makeLogTag(MyWidgetRemoteViewsFactory.class);

//    private ArrayList listItemList = new ArrayList();
    private Context mContext = null;
    private int mAppWidgetId;
    private Cursor mCursor;


    public MyWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        LogHelper.i(TAG, "MyWidgetRemoteViewsFactory.ctor, mAppWidgetId=", mAppWidgetId);

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        LogHelper.i(TAG, "OnDataSetChanged");
        if (mCursor != null) {
            mCursor.close();
        }

        String[] projectionEvents =
                new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.ALL_DAY,
                        CalendarContract.Events.CALENDAR_ID};

        Calendar timeAtMidnight = Calendar.getInstance();
        timeAtMidnight.set(Calendar.HOUR_OF_DAY, 0);
        timeAtMidnight.set(Calendar.MINUTE, 0);
        timeAtMidnight.set(Calendar.SECOND, 0);
        timeAtMidnight.set(Calendar.MILLISECOND, 0);
        long todayAtMidnightInMs = timeAtMidnight.getTimeInMillis();
        timeAtMidnight.add(Calendar.MONTH, Configuration.getLimitNumberOfMonths(mContext, mAppWidgetId));
        long threeMonthsHenceAtMidnightInMs = timeAtMidnight.getTimeInMillis();
        String selection;
        String[] calendarsListArray = Configuration.getCalendarsList(mContext, mAppWidgetId);
        String qnMarks = getQuestionMarks(calendarsListArray.length);
        selection = CalendarContract.Events.DTSTART + " > ?" + " AND " + CalendarContract.Events.DTSTART + " < ? AND " + CalendarContract.Events.CALENDAR_ID + " IN ("+ qnMarks + ")";



        String[] selectionArgs = new String[calendarsListArray.length + 2];
        selectionArgs[0] = Long.toString(todayAtMidnightInMs);
        selectionArgs[1] = Long.toString(threeMonthsHenceAtMidnightInMs);
        for (int i = 0 ; i < calendarsListArray.length; i++) {
            selectionArgs[i+2] = calendarsListArray[i];
        }

        // Check we have necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_CALENDAR);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Start activity to ask the user
                mContext.startActivity(new Intent(mContext, GetPermissionsActivity.class));
                LogHelper.i(TAG, "no permissions" );
                return;
            }
        }

        final long identityToken = Binder.clearCallingIdentity();
        mCursor =
                mContext.getContentResolver().
                        query(CalendarContract.Events.CONTENT_URI,
                                projectionEvents,
                                selection,
                                selectionArgs,
                                CalendarContract.Events.DTSTART + " ASC");

        Binder.restoreCallingIdentity(identityToken);

    }

    private String getQuestionMarks(int len) {
        if (len == 0) {
            return new String();
        }
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        //return position;
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.collection_widget_list_item);

        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        String title = mCursor.getString(1);
        boolean isAllDayEvent = mCursor.getInt(3) == 1;
        long dtstart = mCursor.getLong(2);
        String timeString = Utils.getEventTimeString(dtstart,isAllDayEvent);
        long daysToGo = Utils.getDaysToGo(dtstart);
        remoteView.setTextViewText(R.id.widgetItemTitle,title);
        remoteView.setTextViewText(R.id.widgetItemTime,timeString);
        remoteView.setTextViewText(R.id.widgetItemDays,Long.toString(daysToGo));
        if (daysToGo < 1 ) {
            remoteView.setTextColor(R.id.widgetItemDays, Color.RED);
        } else if (daysToGo < 8) {
            remoteView.setTextColor(R.id.widgetItemDays, Color.YELLOW);
        } else {
            remoteView.setTextColor(R.id.widgetItemDays, Color.WHITE);
        }

        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        long calendarEventId = mCursor.getLong(0);
        extras.putInt(CalendarCountdownAppWidget.EXTRA_ITEM_LIST_POSTION, position);
        extras.putLong(CalendarCountdownAppWidget.EXTRA_ITEM_EVENT_ID, calendarEventId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteView.setOnClickFillInIntent(R.id.widgetItemDays, fillInIntent);
        return remoteView;
    }
}