package de.dastuhl.hours.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.dastuhl.hours.R;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    // required Constructor
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
                getString(R.string.pref_max_value_days_key));
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
                getString(R.string.pref_max_value_weeks_key));
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
                getString(R.string.pref_max_value_months_key));
        onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
                getString(R.string.pref_max_value_years_key));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            pref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
