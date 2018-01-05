package uk.me.asbridge.calendarcountdown;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AsbridgeD on 21-Dec-17.
 */

public class Configuration {
    private static final String TAG = LogHelper.makeLogTag(Configuration.class);
    public static final String PREFS_NAME = "uk.me.asbridge.calendarcountdown.CalendarCountdownAppWidget";

    public static int getLimitNumberOfMonths (Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        int numMonths = sharedPref.getInt(Integer.toString(appWidgetId)+"LIMIT_NUM_MONTHS_PREF", 3);
        LogHelper.i(TAG, "getLimitNumberOfMonths for id", appWidgetId, " = ", numMonths);
        return numMonths;
    }

    static void setLimitNumberOfMonths(Context context, int appWidgetId, int numMonths) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putInt(Integer.toString(appWidgetId)+"LIMIT_NUM_MONTHS_PREF", numMonths);
        prefs.apply();
        LogHelper.i(TAG, "setLimitNumberOfMonths for id", appWidgetId, " to ", numMonths, " = ", getLimitNumberOfMonths(context, appWidgetId));
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        // remove all the keys for this widget id
        // prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void setTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putString(Integer.toString(appWidgetId)+"TITLE_PREF", text);
        prefs.apply();
        LogHelper.i(TAG, "setTitlePref for id", appWidgetId, " to ", text, " = ", getTitlePref(context, appWidgetId));
    }

    public static String getTitlePref(Context context, int appWidgetId)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        String title = sharedPref.getString(Integer.toString(appWidgetId)+"TITLE_PREF", "Coming events");
        LogHelper.i(TAG, "getTitlePref for id", appWidgetId, " = ", title);
        return title;
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void setCalendarsListPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(Configuration.PREFS_NAME, 0).edit();
        prefs.putString(Integer.toString(appWidgetId)+"CALENDAR_LIST_PREF", text);
        prefs.putBoolean(Integer.toString(appWidgetId)+"CALENDAR_LIST_SET", true);
        prefs.apply();
        LogHelper.i(TAG, "setCalendarsListPref for id", appWidgetId, " to ", text, " = ", getCalendarsListPref(context, appWidgetId));
    }

    public static boolean getIsCalendarsListSet(Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean calendarsListIsSet = sharedPref.getBoolean(Integer.toString(appWidgetId) + "CALENDAR_LIST_SET", false);
        return calendarsListIsSet;
    }


    public static String getCalendarsListPref(Context context, int appWidgetId)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        String calendarsList = sharedPref.getString(Integer.toString(appWidgetId)+"CALENDAR_LIST_PREF", "");
        LogHelper.i(TAG, "getCalendarsListPref for id", appWidgetId, " = ", calendarsList);
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
