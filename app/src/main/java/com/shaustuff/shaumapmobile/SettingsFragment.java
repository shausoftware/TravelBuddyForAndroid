package com.shaustuff.shaumapmobile;

import android.preference.PreferenceFragment;
import android.preference.ListPreference;
import android.preference.Preference;

import android.os.Bundle;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference("pref_alert_poll_frequency");
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                ShauMapApplication shauMapApplication = (ShauMapApplication) getActivity().getApplicationContext();
                shauMapApplication.changeAlertPollTime(newValue.toString());
                return true;
            }
        });
    }
}