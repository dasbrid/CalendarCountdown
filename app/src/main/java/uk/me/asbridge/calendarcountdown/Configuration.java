package uk.me.asbridge.calendarcountdown;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AsbridgeD on 21-Dec-17.
 */

public class Configuration {
    private static final String TAG = LogHelper.makeLogTag(Configuration.class);
    public static final String PREFS_NAME = "uk.me.asbridge.calendarcountdown.CalendarCountdownAppWidget";
    // number of months to liit display to
    public static final String KEY_LIMIT_NUM_MONTHS_PREF = "LIMIT_NUM_MONTHS_PREF";
    // title to display
    public static final String KEY_TITLE_PREF = "TITLE_PREF";
    // list of calendars to be displayed (string - comma separated calendar IDs )
    public static final String KEY_CALENDAR_LIST_PREF = "CALENDAR_LIST_PREF";
    // has the list of calendars been configured? Used to select ALL calendars if not yet configured
    public static final String KEY_CALENDAR_LIST_SET = "CALENDAR_LIST_SET";

    private static String getkeyString(int appWidgetId, String key) {
        return Integer.toString(appWidgetId)+"_"+key;
    }

    public static int getLimitNumberOfMonths (Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        int numMonths = sharedPref.getInt(getkeyString(appWidgetId,KEY_LIMIT_NUM_MONTHS_PREF), 3);
        return numMonths;
    }

    static void setLimitNumberOfMonths(Context context, int appWidgetId, int numMonths) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putInt(getkeyString(appWidgetId,KEY_LIMIT_NUM_MONTHS_PREF), numMonths);
        prefs.apply();
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        // remove all the keys for this widget id
        prefs.remove(getkeyString(appWidgetId,KEY_LIMIT_NUM_MONTHS_PREF));
        prefs.remove(getkeyString(appWidgetId,KEY_TITLE_PREF));
        prefs.remove(getkeyString(appWidgetId,KEY_CALENDAR_LIST_PREF));
        prefs.remove(getkeyString(appWidgetId,KEY_CALENDAR_LIST_SET));
        prefs.apply();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void setTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putString(getkeyString(appWidgetId,KEY_TITLE_PREF), text);
        prefs.apply();
    }

    public static String getTitlePref(Context context, int appWidgetId)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        String title = sharedPref.getString(getkeyString(appWidgetId, KEY_TITLE_PREF), "Coming events");
        return title;
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void setCalendarsListPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putString(getkeyString(appWidgetId, KEY_CALENDAR_LIST_PREF), text);
        prefs.putBoolean(getkeyString(appWidgetId, KEY_CALENDAR_LIST_SET), true);
        prefs.apply();
    }

    public static boolean getIsCalendarsListSet(Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean calendarsListIsSet = sharedPref.getBoolean(getkeyString(appWidgetId,  KEY_CALENDAR_LIST_SET), false);
        return calendarsListIsSet;
    }


    public static String getCalendarsListPref(Context context, int appWidgetId)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        String calendarsList = sharedPref.getString(getkeyString(appWidgetId, KEY_CALENDAR_LIST_PREF), "");
        return calendarsList;
    }

    public static String[] getCalendarsList(Context context, int appWidgetId) {
        String calendarsListString = getCalendarsListPref(context, appWidgetId);
        if (calendarsListString.isEmpty()) {
            return new String[0];
        }
        String[] calendarsListArray = calendarsListString.split(",");
        return calendarsListArray;
    }


}
