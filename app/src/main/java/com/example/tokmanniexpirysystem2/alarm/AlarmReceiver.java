package com.example.tokmanniexpirysystem2.alarm;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;


import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.tokmanniexpirysystem2.MainActivity;
import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.SettingsActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


// https://codelabs.developers.google.com/codelabs/android-training-alarm-manager/index.html?index=..%2F..android-training#3
public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent == null || intent.getExtras() == null || intent.getExtras().getString(AlarmHelper.ACTION_TYPE) == null) {
            return;
        }
       if(intent.getExtras().getString(AlarmHelper.ACTION_TYPE).equals(AlarmHelper.START_ALARM)) {
           Calendar nowCalendar = Calendar.getInstance();
           int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
           SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
           int prefNotifyStartHour = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_START_HOUR, 8);
           int prefNotifyEndHour = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_END_HOUR, 20);
           if(nowHour > prefNotifyStartHour   &&  nowHour < prefNotifyEndHour ) {
               AlarmHelper.getInstance(context).deliverNotification(context);
               if(AlarmHelper.getInstance(context).isSettingsRingerOn()) {
                   if(!AlarmHelper.getInstance(context).isAppOnForeground(context)) {
                       AlarmHelper.getInstance(context).playSound(context);
                   }
               }
           }
       }

       if(intent.getExtras().getString(AlarmHelper.ACTION_TYPE).equals(AlarmHelper.STOP_ALARM)) {
           AlarmHelper.getInstance(context).stopSound(context);
       }
    }






}

