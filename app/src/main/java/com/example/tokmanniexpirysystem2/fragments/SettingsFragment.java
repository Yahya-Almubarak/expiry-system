package com.example.tokmanniexpirysystem2.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.SettingsActivity;

import java.util.Set;


public class SettingsFragment extends PreferenceFragmentCompat {
    Boolean ringeToneOnValue;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity() /* Activity context */);
        SeekBarPreference notificationRepeatitionPeriod = findPreference(SettingsActivity.KEY_PREF_NOTIF_REPETITION_PERIOD);
        SeekBarPreference notificationStartHour = findPreference(SettingsActivity.KEY_PREF_NOTIF_START_HOUR);
        SeekBarPreference notificationEndHour = findPreference(SettingsActivity.KEY_PREF_NOTIF_END_HOUR);
        SwitchPreference ringToneOn =  findPreference(SettingsActivity.KEY_PREF_RINGER_ON);
        SeekBarPreference ringToneDuration = findPreference(SettingsActivity.KEY_PREF_RING_TONE_DURATION);

        if(notificationStartHour != null) {
            notificationStartHour.setMin(4);
            notificationStartHour.setMax(22);
            notificationStartHour.setSeekBarIncrement(1);
            notificationStartHour.setShowSeekBarValue(true);
            notificationStartHour.setUpdatesContinuously(true);
            notificationStartHour.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue != null) {
                        SeekBarPreference notificationStartHour = (SeekBarPreference) preference;
                        Integer notificationEndHourValue = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_END_HOUR, 22);
                        Integer notificationStartHourValue = (Integer) newValue;

                        if(notificationStartHourValue >= (notificationEndHourValue -2)) {
                            notificationStartHour.setValue(notificationEndHourValue.intValue() - 2);
                            return false;
                        }
                    }

                    return true;
                }
            });
        }


        if(notificationEndHour != null) {
            notificationEndHour.setMin(4);
            notificationEndHour.setMax(22);
            notificationEndHour.setSeekBarIncrement(1);
            notificationEndHour.setShowSeekBarValue(true);
            notificationStartHour.setUpdatesContinuously(true);
            notificationEndHour.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue != null) {
                        SeekBarPreference notificationEndHour = (SeekBarPreference) preference;
                        Integer notificationStartHourValue = sharedPreferences.getInt(SettingsActivity.KEY_PREF_NOTIF_START_HOUR, 6);
                        Integer notificationEndHourValue = (Integer) newValue;
                        if(notificationStartHourValue >= (notificationEndHourValue -2) ) {
                            notificationEndHour.setValue(notificationStartHourValue.intValue() + 2);
                            return false;
                        }
                    }
                    return true;
                }
            });
        }

        if(notificationRepeatitionPeriod != null) {
            notificationRepeatitionPeriod.setMin(0);
            notificationRepeatitionPeriod.setMax(8);
            notificationRepeatitionPeriod.setSeekBarIncrement(1);
            notificationRepeatitionPeriod.setShowSeekBarValue(true);
            notificationRepeatitionPeriod.setUpdatesContinuously(true);
        }

        if(ringToneOn != null) {
            ringToneOn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                        ringToneDuration.setEnabled((Boolean)newValue);
                    return true;
                }
            });
        }

        if(ringToneDuration != null) {
            ringToneDuration.setMin(0);
            ringToneDuration.setMax(120);
            ringToneDuration.setSeekBarIncrement(10);
            ringToneDuration.setShowSeekBarValue(true);
            ringToneDuration.setEnabled(sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_RINGER_ON, true));
            ringToneDuration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int value = ((Integer)newValue).intValue();
                    if(value%10 != 0){
                        ((SeekBarPreference)preference).setValue(Math.floorDiv(value, 10) * 10);
                        return false;
                    }
                    return true;
                }
            });
        }

    }
}
