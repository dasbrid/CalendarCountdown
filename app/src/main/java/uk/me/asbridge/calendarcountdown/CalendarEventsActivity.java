package uk.me.asbridge.calendarcountdown;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarEventsActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(CalendarEventsActivity.class);
    ListView listViewCalendars;
    ListView listViewEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_events);

        // Get ListView object from xml
        listViewCalendars = (ListView) findViewById(R.id.lvcalendars);
        listViewEvents = (ListView) findViewById(R.id.lvevents);

        // Check we have necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(CalendarEventsActivity.this,
                    Manifest.permission.READ_CALENDAR);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Start activity to ask the user
                startActivity(new Intent(CalendarEventsActivity.this, GetPermissionsActivity.class));
                finish();
                return;
            }
        }
        long mainID = -1;
        ArrayList<String> calendarnames = new ArrayList<>();
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        Cursor calCursor =
                getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            mainID = calCursor.getLong(0);
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(2);
                // ...
                calendarnames.add(displayName);
                LogHelper.i(TAG, "found calendar: ",id, "-", displayName, "-", calCursor.getString(2));
            } while (calCursor.moveToNext());
        }

        LogHelper.i(TAG, "Found ", calendarnames.size(), " calendars");
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> CalendarsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, calendarnames);

        // Assign adapter to ListView
        listViewCalendars.setAdapter(CalendarsAdapter);

        ArrayList<String> eventnames = new ArrayList<>();
        String[] projectionEvents =
                new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.ALL_DAY};


        Calendar todayAtMidnight = Calendar.getInstance();
        todayAtMidnight.set(Calendar.HOUR_OF_DAY, 0);
        todayAtMidnight.set(Calendar.MINUTE, 0);
        todayAtMidnight.set(Calendar.SECOND, 0);
        todayAtMidnight.set(Calendar.MILLISECOND, 0);
        long todayAtMidnightInMs = todayAtMidnight.getTimeInMillis();

        String selection;
        selection = CalendarContract.Events.DTSTART + " > ?";
        String [] selectionArgs = new String [] {Long.toString(todayAtMidnightInMs)};

        /* use this to only get one calendar
        String selection = CalendarContract.Events.CALENDAR_ID + "=  ?";
        String[] selectionArgs = new String[] {Long.toString(mainID)};
        */


        Cursor eventsCursor =
                getContentResolver().
                        query(CalendarContract.Events.CONTENT_URI,
                                projectionEvents,
                                selection,
                                selectionArgs,
                                CalendarContract.Events.DTSTART + " ASC");
        if (eventsCursor.moveToFirst()) {
            do {
                long id = eventsCursor.getLong(0);
                String title = eventsCursor.getString(1);
                long dtstart = eventsCursor.getLong(2);
                boolean isAllDayEvent = eventsCursor.getInt(3) == 1;
                String eventDateString = getEventTimeString(dtstart, isAllDayEvent);
                long daysToGo = getDaysToGo(dtstart);
                eventnames.add(Long.toString(daysToGo) +  ":" + title + "@" + eventDateString);
                LogHelper.i(TAG, "found event: ",id, "-", title);
            } while (eventsCursor.moveToNext());
        }

        LogHelper.i(TAG, "Found ", eventnames.size(), " events");
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> EventsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, eventnames);

        // Assign adapter to ListView
        listViewEvents.setAdapter(EventsAdapter);

    }

    private String getEventTimeString(long eventTime, boolean isAllDayEvent) {
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss)");

        Date eventDate = new Date(eventTime);

        if (isAllDayEvent) {
            return sdfDay.format(eventDate);
        } else {
            return sdfTime.format(eventDate);
        }
    }

    private long getDaysToGo(long eventTime) {
        Date eventDate = new Date(eventTime);
        Calendar target = Calendar.getInstance();
        target.setTime(eventDate);
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);


        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long diffInDays = (target.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 60 / 60 /24;
        return diffInDays;

    }
}
