package ru.droidwelt.prototype4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static void main(String[] args) {
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Appl.SETTING_MODE_GLOBAL) {
            addPreferencesFromResource(R.xml.pref);
            setTitle(getString(R.string.s_setting_programm));
        } else {
            addPreferencesFromResource(R.xml.pref_mes);
            setTitle(getString(R.string.s_setting_messages));
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("font_size"))
        { MainActivity.setMastRestartedOnResume(true);
        }
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        // Registers a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregisters the listener set in onResume().
        // It's best practice to unregister listeners when your app isn't using them to cut down on
        // unnecessary system overhead. You do this in onPause().
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
*/

}
