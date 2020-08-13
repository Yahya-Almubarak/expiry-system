package com.example.tokmanniexpirysystem2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tokmanniexpirysystem2.fragments.SettingsFragment;

import java.security.PublicKey;

public class SettingsActivity extends AppCompatActivity {


    public static final String KEY_PREF_NOTIF_REPETITION_PERIOD = "notification_repeatition_period";
    public static final String KEY_PREF_NOTIF_START_HOUR = "notification_start_hour";
    public static final String KEY_PREF_NOTIF_END_HOUR = "notification_end_hour";
    public static final String KEY_PREF_RING_TONE_DURATION = "ring_tone_duration";
    public static final String KEY_PREF_RINGER_ON = "ring_tone_on";
    public static final String KEY_PREF_USER_AUTHENTICATION = "user_authentication_on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
