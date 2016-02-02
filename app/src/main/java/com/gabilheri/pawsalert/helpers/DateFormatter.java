package com.gabilheri.pawsalert.helpers;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 9/22/15.
 */
public final class DateFormatter {

    private static final SimpleDateFormat serverDateFormat;
    private static final SimpleDateFormat simpleDateFormat;
    private static final SimpleDateFormat prettyDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        prettyDateFormat = new SimpleDateFormat("MMM, dd yyyy", Locale.getDefault());
        serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    }

    public static String format(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String prettyFormat(Date date) {
        return prettyDateFormat.format(date);
    }

    public static Date getDateFromString(@NonNull String dateString) {
        try {
            return serverDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringServerDate(Date date) {
        return serverDateFormat.format(date);
    }

    public static String prettyFormatServerStringDate(String dateString) {
        return prettyFormat(getDateFromString(dateString));
    }
}
