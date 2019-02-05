package com.emrhmrc.cash10.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Functions {

    public static final String DateFormatString = "d MMM EEE yyyy HH:mm";

    public static Calendar TextToCalendar(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatString);
        Date date = null;
        try {
            date = sdf.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String DateTimeFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Date sourceDate = null;
        try {
            sourceDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat targetFormat = new SimpleDateFormat(DateFormatString);
        return targetFormat.format(sourceDate);

    }

    public static String DateToText() {

        Date sourceDate = Calendar.getInstance().getTime();
        SimpleDateFormat targetFormat = new SimpleDateFormat(DateFormatString);
        return targetFormat.format(sourceDate);

    }
}
