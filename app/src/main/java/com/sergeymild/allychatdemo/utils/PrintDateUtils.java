package com.sergeymild.allychatdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sergeyMild on 17/01/16.
 */
public class PrintDateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final Calendar todayCalendar = Calendar.getInstance();
    private static final Calendar workCalendar = Calendar.getInstance();

    public static String getFormattedDateForRoomLastMessage(long createdAtMills) {
        workCalendar.setTimeInMillis(createdAtMills);

        if (workCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                && workCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
            dateFormat.applyPattern("HH:mm");
        } else if (workCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                && workCalendar.get(Calendar.DAY_OF_YEAR) != todayCalendar.get(Calendar.DAY_OF_YEAR)) {
            dateFormat.applyPattern("dd MMM, HH:mm");
        } else {
            dateFormat.applyPattern("dd MMM yyyy, HH:mm");
        }
        return dateFormat.format(createdAtMills);
    }
}
