package com.example.tokmanniexpirysystem2.utilities;

import android.content.Context;

import androidx.room.TypeConverter;

import com.example.tokmanniexpirysystem2.R;

public class RoleConverter {
  //  private static RoleConverter mInstance;
  //  private Context mContext;
  /*  public static RoleConverter getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new RoleConverter(context);
        }
        return mInstance;
    } */

  /*private RoleConverter(Context context) {
        mContext = context;
    } */
    @TypeConverter
    public static Role fromString(String value) {
        if (value != null) {
            try {
                return Role.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String stringFromRole(Role value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }


}
