package com.jcedar.chartbills.helper;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by OLUWAPHEMMY on 3/14/2017.
 */

public class FormatUtils {

    public static String dayFormatHelper(int day) {
        String daye;
        if (day < 10) {
            daye = "0"+day;
        } else {
            daye = String.valueOf(day);
        }
        return daye;
    }

    public static String monthFormatHelper(int month) {
        String daye;
        if (month < 10) {
            daye = "0"+(month+1);
        } else {
            daye = String.valueOf(month+1);
        }
        return daye;
    }

    public static long dbDate (String dateStr) {
        long longDate = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            longDate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longDate;
    }

    public static String getTodaysDate () {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String dayy = FormatUtils.dayFormatHelper(day);
        String mont = FormatUtils.monthFormatHelper(month);

        String mDate = dayy+"/"+mont+"/"+year;

        return String.valueOf(FormatUtils.dbDate(mDate));
    }

    public static String getRecyclerReadableDate (long dueDate) {
        String formattedDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(dueDate);
        formattedDate = dateFormat.format(date);

        return formattedDate;
    }

    public static String getReadableDate (long timestamp) {
        Date date = new Date(timestamp);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d, hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long now = System.currentTimeMillis();
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
        String mm = dateFormat.format(date);
        if (timeAgo.equals("Today")) {
            return timeAgo + "";
        } else if (timeAgo.equals("Yesterday")) {
            return timeAgo + "";
        }  else if (timeAgo.equals("Tomorrow")) {
            return timeAgo + "";
        } else
//            return (String) timeAgo +" , " + mm;
            return (String) timeAgo;
    }

    public static String getUpcomingReadableDate (long timestamp, int week, int month, int year) {
        Date date = new Date(timestamp);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM d, hh:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM", Locale.getDefault());


        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = (cal.get(Calendar.MONTH) + 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int weekMonth = cal.get(Calendar.WEEK_OF_MONTH);

        long now = System.currentTimeMillis();
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
        String mm = dateFormat.format(date);
        if (timeAgo.equals("Today")) {
            return timeAgo + "";
        } else if (timeAgo.equals("Yesterday")) {
            return timeAgo + "";
        } else if (timeAgo.equals("Tomorrow")) {
            return timeAgo + "";
        } else if ((cYear == year) && (cMonth == month) && (weekMonth == week)) {
            return "This Week, " + mm;
        } else if ((cYear == year) && (cMonth == month) && ((weekMonth + 1) == week)) {
            return "Next Week, " + mm;
        } else
            return (String) timeAgo;
    }

    public static int setWeekFirstDay (String myWeekFirst) {
        int firstDay = 0;
        Calendar cc = Calendar.getInstance();
        if (myWeekFirst.equalsIgnoreCase("sunday")){
            firstDay = Calendar.SUNDAY;
        }else if (myWeekFirst.equalsIgnoreCase("monday")){
            firstDay = Calendar.MONDAY;
        }else if (myWeekFirst.equalsIgnoreCase("tuesday")){
            firstDay = Calendar.TUESDAY;
        }else if (myWeekFirst.equalsIgnoreCase("wednesday")){
            firstDay = Calendar.WEDNESDAY;
        }else if (myWeekFirst.equalsIgnoreCase("thursday")){
            firstDay = Calendar.THURSDAY;
        }else if (myWeekFirst.equalsIgnoreCase("friday")){
            firstDay = Calendar.FRIDAY;
        }else if (myWeekFirst.equalsIgnoreCase("saturday")){
            firstDay = Calendar.SATURDAY;
        }

        return firstDay;
    }

}
