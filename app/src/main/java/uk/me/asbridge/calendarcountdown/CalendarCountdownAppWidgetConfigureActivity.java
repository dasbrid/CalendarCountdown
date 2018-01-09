package uk.me.asbridge.calendarcountdown;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link CalendarCountdownAppWidget CalendarCountdownAppWidget} AppWidget.
 */
public class CalendarCountdownAppWidgetConfigureActivity extends Activity {
    private static final String TAG = LogHelper.makeLogTag(CalendarCountdownAppWidgetConfigureActivity.class);

    private static final String PREF_PREFIX_KEY = "appwidget_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private  EditText etTitle;
    private  EditText etNumMonths;
    private ListView mlistViewCalendars;
    private CalendarsAdapter mCalendarsAdapter;
    private ArrayList<Calendar> mCalendars;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CalendarCountdownAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String title = etTitle.getText().toString();
            Configuration.setTitlePref(context, mAppWidgetId, title);

            String numMonthsString = etNumMonths.getText().toString();
            int numMonths = Integer.parseInt(numMonthsString);
            Configuration.setLimitNumberOfMonths(context, mAppWidgetId, numMonths);

            SparseBooleanArray checked = mlistViewCalendars.getCheckedItemPositions();
            LogHelper.i(TAG, checked.size() , " selected");
            String selectedCalendars = new String();
            for (int i = 0; i < checked.size(); i++) {
                // Item position in adapter
                int position = checked.keyAt(i);
                LogHelper.i(TAG, "i=",i, " position=", position);
                if (checked.valueAt(i)) {
                    Calendar selectedItem = mCalendars.get(position);
                    if (i !=0) {
                        selectedCalendars+=",";
                    }
                    selectedCalendars = selectedCalendars + selectedItem.getId();
                }
            }
            Configuration.setCalendarsListPref(context, mAppWidgetId, selectedCalendars);
            LogHelper.i(TAG, "SelectedCalendars = ", selectedCalendars);


            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            //N.B.: we want to launch this intent to our AppWidgetProvider!
            // Send broadcaset for first update
            Intent firstUpdate = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), CalendarCountdownAppWidget.class);
            int[] appWidgetIds = new int[] {mAppWidgetId};
            firstUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(firstUpdate);
            setResult(RESULT_OK, firstUpdate);


            finish();
        }
    };

    public CalendarCountdownAppWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.calendar_countdown_app_widget_configure);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etNumMonths = (EditText) findViewById(R.id.etNumMonths);
        findViewById(R.id.OK_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        etTitle.setText(Configuration.getTitlePref(this,mAppWidgetId));
        int numMonths = Configuration.getLimitNumberOfMonths(this,mAppWidgetId);
        etNumMonths.setText(Integer.toString(numMonths));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int permissionCheck = ContextCompat.checkSelfPermission(CalendarCountdownAppWidgetConfigureActivity.this,
                    Manifest.permission.READ_CALENDAR);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // We do not have necessary permission. Start activity to ask the user
                startActivity(new Intent(CalendarCountdownAppWidgetConfigureActivity.this, GetPermissionsActivity.class));
                finish();
                return;
            }
        }

        // The list of already selected calendars (in case we are opening by clicking on the widget)
        mCalendars = new ArrayList<>();
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
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(2);
                Calendar calendar = new Calendar(displayName, id);
                mCalendars.add(calendar);
            } while (calCursor.moveToNext());
        }
        calCursor.close();
        mCalendarsAdapter = new CalendarsAdapter(this, mCalendars);
        mlistViewCalendars = (ListView) findViewById(R.id.lvCalendars);
        // Assign adapter to ListView
        mlistViewCalendars.setAdapter(mCalendarsAdapter);
        mlistViewCalendars.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setCheckedCalendars();
    }

    /**
     * Complicated way of setting items in the listview to be checked
     * based on the current settings (stored in shared prefs)
     */
    private void setCheckedCalendars() {
        // the ids of the currently selected calendars, from shared prefs, as an array of strings
        // Note: these are android calendar _IDs (not indices in the list view)
        String[] selectedCalendars = Configuration.getCalendarsList(this, mAppWidgetId);
        boolean calendarListIsSet = Configuration.getIsCalendarsListSet(this, mAppWidgetId);
        LogHelper.i(TAG, selectedCalendars.length, " cals");
        // loop through each calendar in the list view. Set checked if it is currently selected
        for (int c = 0; c < mCalendars.size(); c++) {
            if ( !calendarListIsSet ) {
                mlistViewCalendars.setItemChecked(c, true);
            } else {
                long id = mCalendars.get(c).getId();
                String idString = Long.toString(id);
                // loop through the selected calendars, looking for the current calendar
                // stop looping if we find the calendar
                int i;
                for (i = 0; i < selectedCalendars.length && !selectedCalendars[i].equals(idString); i++)
                    ; // Note: one line loop;
                // i will equal number of selected calendars if the current calendar was NOT found (we reached the end of the list)
                if (i < selectedCalendars.length) {
                    mlistViewCalendars.setItemChecked(c, true);
                }
            }

        }
    }
}

