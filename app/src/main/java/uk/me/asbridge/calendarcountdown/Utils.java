package uk.me.asbridge.calendarcountdown;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AsbridgeD on 21-Dec-17.
 */

public class Utils {

    /**
     * get formatted string representing the time of a calendar event
     * @param eventTime time in ms since epoch
     * @param isAllDayEvent true if this is an all day event (time will not be added to output string)
     * @return string showing formatted time
     */
    public static String getEventTimeString(long eventTime, boolean isAllDayEvent) {
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        Date eventDate = new Date(eventTime);

        if (isAllDayEvent) {
            return sdfDay.format(eventDate);
        } else {
            return sdfTime.format(eventDate);
        }
    }

    /**
     * Return number of days to go until a time in ms since epoch
     * @param eventTime time in ms since epoch
     * @return number of days to go (negative if event passed)
     */
    public static long getDaysToGo(long eventTime) {
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
