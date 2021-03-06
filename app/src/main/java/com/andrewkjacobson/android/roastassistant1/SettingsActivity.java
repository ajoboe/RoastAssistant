package com.andrewkjacobson.android.roastassistant1;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_TEMP_CHECK_FREQ = "temperature_check_frequency";
    public static final String KEY_PREF_ALLOWED_TEMP_CHANGE = "allowed_temp_change";
    public static final String KEY_PREF_STARTING_TEMPERATURE = "starting_temperature";
    public static final String KEY_PREF_STARTING_POWER = "starting_power";
    public static final String KEY_PREF_ROAST_TIME_ADDEND = "roast_time_addend";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
//        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}