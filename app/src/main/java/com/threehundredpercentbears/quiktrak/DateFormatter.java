package com.threehundredpercentbears.quiktrak;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DateFormatter {
    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";

    public static String dateToString(Date date) {
        Objects.requireNonNull(date);

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);

        return format.format(date);
    }

    public static Date stringToDate(String dateAsString) {
        Objects.requireNonNull(dateAsString);

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);

        Date date = null;
        try {
            date = format.parse(dateAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String intsToStringDate(int day, int month, int year) {
        return String.format(Locale.ENGLISH, "%02d/%02d/%04d", month, day, year);
    }
}
