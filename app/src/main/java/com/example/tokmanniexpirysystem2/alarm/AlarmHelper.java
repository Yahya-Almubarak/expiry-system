package com.example.tokmanniexpirysystem2.alarm;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.tokmanniexpirysystem2.LoginActivity;
import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.SettingsActivity;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.Product;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class AlarmHelper {
    public static final String ACTION_TYPE = "action";
    public static final String START_ALARM = "start.alarm";
    public static final String STOP_ALARM = "stop.alarm";
    private static final String TAG = "Alarm Receiver";

    private Context context;
    private static AlarmHelper mInstance;
    private SharedPreferences sharedPreferences;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private NotificationManager mNotificationManager;
    private final PendingIntent notifyPendingIntent;
    private final AlarmManager alarmManager;

    private static Uri alarmUri;
    private static Ringtone mRingtone;


    private AlarmHelper(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mNotificationManager = createNotificationChannel();
        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        notifyIntent.putExtra(AlarmHelper.ACTION_TYPE, AlarmHelper.START_ALARM);
       // boolean alarmUp = (PendingIntent.getBroadcast(context, NOTIFICATION_ID, notifyIntent,
       //         PendingIntent.FLAG_NO_CREATE) != null);
        notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        mRingtone = initializeRingTone(context);
    }

    public static synchronized AlarmHelper getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AlarmHelper(mCtx);
        }
        return mInstance;
    }

    public NotificationManager createNotificationChannel() {

        // Create a notification manager object.
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);


        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            context.getResources().getString(R.string.notification_channal_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (context.getResources().getString(R.string.notification_channal_description));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        return notificationManager;
    }

    public void deliverNotification(Context context) {

        Intent contentIntent = new Intent(context, LoginActivity.class);

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.tokmanni_logo_108x108)
                .setContentTitle(context.getResources().getString(R.string.alert_title))
                .setContentText(context.getResources().getString(R.string.alert_content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
       if(!isAppOnForeground(context)) {
            builder.setContentIntent(contentPendingIntent);
        }

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }


    public void setAlarm() {
        class GetProducts extends AsyncTask<Void, Void, List<Product>> {

            @Override
            protected List<Product> doInBackground(Void... voids) {
                List<Product> productsList = DatabaseClient
                        .getInstance(context.getApplicationContext())
                        .getAppDatabase()
                        .productDao()
                        .getAll();
                return productsList;
            }
            @Override
            protected void onPostExecute(List<Product> products) {
                super.onPostExecute(products);
                postExecuteGetProducts(products);
            }
        }
        GetProducts gp = new GetProducts();
        gp.execute();
    }

    private void postExecuteGetProducts(List<Product> products) {
        if(products == null || products.isEmpty()) {
            return;
        }
        Collections.sort(products, Product.compareAlarmDate);
        long triggerTime;
        Date alarmDate = null;
        Product product = products.get(0);
        if(products.stream().filter(p -> p.getProductAlarmDate().before(new Date())).findAny().isPresent()) {
            alarmDate = new Date();
        } else {
            alarmDate = product.getProductAlarmDate();
        }

        Calendar calendar= Calendar.getInstance();
        calendar.setTime(alarmDate);
        int notifyStartHour = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_START_HOUR, 8);
        calendar.set(Calendar.HOUR_OF_DAY, notifyStartHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Calendar nowCalendar = Calendar.getInstance();
        int nowHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        nowCalendar.set(Calendar.HOUR_OF_DAY, notifyStartHour);
        nowCalendar.set(Calendar.MINUTE, 0);
        nowCalendar.set(Calendar.SECOND, 0);

        if(calendar.compareTo(nowCalendar) == 0) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 2);
        }
        triggerTime = calendar.getTimeInMillis();
        long repeatInterval = 60 * 60 * 1000 * sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_REPETITION_PERIOD, 1);
     //   triggerTime = SystemClock.elapsedRealtime();
      //  repeatInterval = 50000;
        if (alarmManager != null) {
            alarmManager.setInexactRepeating
                    (AlarmManager.RTC_WAKEUP,
                            triggerTime, repeatInterval, notifyPendingIntent);
            String toast = " next alarm at : " + new Date(triggerTime);
            Toast.makeText(context,toast , Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void  cancelAlarm() {
        mNotificationManager.cancelAll();
       if (alarmManager != null) {
          alarmManager.cancel(notifyPendingIntent);

            // Tell the receiver to stop playing mRingtone
            Intent cancelAlarmIntent = new Intent(context, AlarmReceiver.class);
            cancelAlarmIntent.putExtra(AlarmHelper.ACTION_TYPE, AlarmHelper.STOP_ALARM);
           context.sendBroadcast(cancelAlarmIntent);


        }
    }

    public void stopSound(Context context) {
        if (mRingtone.isPlaying()) {
            mRingtone.stop();
        }
    }

    public void playSound(Context context) {

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        long ringToneDuration = 1000 * sharedPreferences.getInt(SettingsActivity.KEY_PREF_RING_TONE_DURATION, 30);
        if(mRingtone.isPlaying()) {
            mRingtone.stop();
        }
        mRingtone.play();


        new CountDownTimer( ringToneDuration, ringToneDuration) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                mRingtone.stop();
            }
        }.start();

    }

    public Ringtone initializeRingTone(Context context) {
        Ringtone ringtone = null;
        if(alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }

        if(ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, alarmUri);
        }
        return ringtone;
    }

    public boolean isSettingsRingerOn() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_RINGER_ON, false);
    }

    public boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
