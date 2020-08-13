package com.example.tokmanniexpirysystem2.utilities;


import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
   // public static final String DATE_OF_EXPIRE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_OF_EXPIRE_FORMAT = "dd.MM.yyyy";
    static DateFormat df = new SimpleDateFormat(DATE_OF_EXPIRE_FORMAT);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date value) {

        return value == null ? null : df.format(value);
    }
}
