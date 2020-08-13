package com.example.tokmanniexpirysystem2.utilities;

import androidx.room.TypeConverter;


public class UserActionConverter {

    @TypeConverter
    public static UserAction fromString(String value) {
        if (value != null) {
            try {
                return UserAction.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String stringFromUserAction(UserAction value) {
        return value.toString();
    }
}
