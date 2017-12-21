package uk.me.asbridge.calendarcountdown;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AsbridgeD on 21-Dec-17.
 */

public class Configuration {

    public static final String PREFS_NAME = "uk.me.asbridge.calendarcountdown.CalendarCountdownAppWidget";

    public static int getLimitNumberOfMonths () {
        return 3;
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        // remove all the keys for this widget id
        // prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }
}
