package com.sbtn.androidtv.utils;

/**
 * Created by hoanguyen on 5/9/16.
 */
public class DateTimeUtils {
    public static int convertTimeStringToSecond(String stringTime) {
        if (stringTime == null || stringTime.length() < 8) {
            return 0;
        }
        int second, h, m, s;
        second = h = m = s = 0;
        try {
            h = Integer.valueOf(stringTime.substring(0, 2));
            m = Integer.valueOf(stringTime.substring(3, 5));
            s = Integer.valueOf(stringTime.substring(6, 8));

            second = h * 3600 + m * 60 + s;
        } catch (NumberFormatException e) {

        }

        return second;
    }

    public static int parseTime(String time) {
        try {
            if (time.equals("") || time.equals("00:00:00")) return 0;
            String[] arr_time = time.split(":");
            if (arr_time.length == 3) {
                int hours = parseInteger(arr_time[0]);
                int minutes = parseInteger(arr_time[1]);
                int seconds = parseInteger(arr_time[2]);
                return hours * 60 * 60 + minutes * 60 + seconds;
            } else if (arr_time.length == 2) {
                int minutes = parseInteger(arr_time[0]);
                int seconds = parseInteger(arr_time[1]);
                return minutes * 60 + seconds;
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    public static int parseInteger(String data) {
        try {
            return Integer.parseInt(data);
        } catch (Exception ex) {
        }
        return 0;
    }
}
