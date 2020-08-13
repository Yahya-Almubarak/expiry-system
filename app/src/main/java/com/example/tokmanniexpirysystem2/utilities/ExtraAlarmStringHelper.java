package com.example.tokmanniexpirysystem2.utilities;

import android.content.Context;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.entities.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

        /*
        <item>None</item>
        <item>1 One Day</item>
        <item>3 Three Days</item>
        <item>10 Ten Days</item>
        <item>15 Fifteen Days</item>
        <item>20 Twenty Days</item>
        <item>30 One Month</item>
        <item>45 One Month and half</item>
        <item>60 Two Months</item>
        <item>90 Three Months</item>
        */

public class ExtraAlarmStringHelper {

    private Context context;
    private static ExtraAlarmStringHelper mInstance;

    List<Integer> extraAlarmInDaysList;
    String[] extraAlarmStringArray;

    private ExtraAlarmStringHelper(Context context) {

        this.context = context;
        extraAlarmInDaysList = new ArrayList<>();
        extraAlarmInDaysList.add(0);
        extraAlarmInDaysList.add(1);
        extraAlarmInDaysList.add(3);
        extraAlarmInDaysList.add(10);
        extraAlarmInDaysList.add(15);
        extraAlarmInDaysList.add(20);
        extraAlarmInDaysList.add(30);
        extraAlarmInDaysList.add(45);
        extraAlarmInDaysList.add(60);
        extraAlarmInDaysList.add(90);
        extraAlarmStringArray = context.getResources().getStringArray(R.array.extra_alarm);
    }

    public static synchronized ExtraAlarmStringHelper getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new ExtraAlarmStringHelper(mCtx);
        }
        return mInstance;
    }

    public List<String>  ListOfLiteralExtraAlarm(Date olderDate, Date newerDate) {
        List<String> list = new ArrayList<String>();
        String[] stringArray = context.getResources().getStringArray(R.array.extra_alarm);

        Calendar olderDateCalander = Calendar.getInstance();
        olderDateCalander.setTime(olderDate);
        Calendar newerDataCalander = Calendar.getInstance();
        newerDataCalander.setTime(newerDate);
        if(olderDateCalander.compareTo(newerDataCalander) >= 0) {
            list.add(stringArray[0]);
            return list;
        }


        long diffInMillisec = newerDate.getTime() - olderDate.getTime();

        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec);

        int noOfItems = 0;

        if (diffInDays < 1) {
            noOfItems = 1;
        } else if (diffInDays < 3) {
            noOfItems = 2;
        } else if (diffInDays < 10) {
            noOfItems = 3;
        } else if (diffInDays < 15) {
            noOfItems = 4;
        } else if (diffInDays < 20) {
            noOfItems = 5;
        } else if (diffInDays < 30) {
            noOfItems = 6;
        } else if (diffInDays < 45) {
            noOfItems = 7;
        } else if (diffInDays < 60) {
            noOfItems = 8;
        } else if (diffInDays < 90) {
            noOfItems = 9;
        } else {
            noOfItems = 10;
        }


            for(int i = 0; i < noOfItems; i++) {
                list.add(stringArray[i]);
            }

        return list;
    }

    public List<String>  ListOfLiteralExtraAlarm() {

        List<String> list = new ArrayList<String>();
        String[] stringArray = context.getResources().getStringArray(R.array.extra_alarm);
        list.addAll(Arrays.asList(stringArray));
        return list;
    }

    public List<String>  ListOfLiteralPassedAlarm(Product product) {
        Date expireDate = product.getProductExpire();
        Date alarmDate =  product.getProductAlarmDate();
        Calendar nowCalendar = Calendar.getInstance();
        Calendar expireDateCalendar = Calendar.getInstance();
        expireDateCalendar.setTime(expireDate);
        Calendar alarmDateCalendar = Calendar.getInstance();
        alarmDateCalendar.setTime(alarmDate);

        String[] stringArray = context.getResources().getStringArray(R.array.extra_alarm);
        List<String> restrictedList = new ArrayList<String>();

        if( nowCalendar.compareTo(expireDateCalendar) > 0) {
            restrictedList.add(stringArray[0]);
            return restrictedList;
        }



        restrictedList = ExtraAlarmStringHelper.getInstance(context).ListOfLiteralExtraAlarm(new Date(), expireDate);
        int indexOfExtraAlarmString = indexOfDaysExtraAlarms(product.getProductExtraAlarm());

        if(!restrictedList.contains(stringArray[indexOfExtraAlarmString])) {
            restrictedList.add(stringArray[indexOfExtraAlarmString]);
        }

       return restrictedList;
    }

    public int indexOfDaysExtraAlarms(int days) {

        int index;
        index = extraAlarmInDaysList.indexOf(days);
        if(index < 0) {
            index = 0;
        }
        return index;
    }

    public String stringOfDaysExtraAlarms(int days) {

        int index = this.indexOfDaysExtraAlarms(days);
        return extraAlarmStringArray[index];
    }


    public int daysOfIndex(int index) {
        int days;
        if(index >= 0 && index <= 9) {
            days = extraAlarmInDaysList.get(index);
        } else {
           days = 0;
        }
        return days;
    }


}
