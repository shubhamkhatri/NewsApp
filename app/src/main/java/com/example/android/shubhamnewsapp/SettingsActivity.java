package com.example.android.shubhamnewsapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;
import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minDate = findPreference(getString(R.string.settings_min_date_key));
            bindPreferenceSummaryToValue(minDate);

            Preference selectSection = findPreference(getString(R.string.settings_select_section_key));
            bindPreferenceSummaryToValue(selectSection);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(stringValue);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Date format must be YYYY-MM-DD", Toast.LENGTH_LONG).show();
                    return false;
                }
                preference.setSummary(stringValue);
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
