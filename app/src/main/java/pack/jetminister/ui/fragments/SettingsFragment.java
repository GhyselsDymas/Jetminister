package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import pack.jetminister.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreference darkLight;
    private Context context;

    private Preference.OnPreferenceChangeListener darkLightListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (darkLight.isChecked()){
                darkLight.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                darkLight.setChecked(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return false;
        }
    };

    public SettingsFragment(){
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if(isDarkMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        addPreferencesFromResource(R.xml.settings);
        darkLight = findPreference("dark_mode");
        context = darkLight.getContext();
        darkLight.setOnPreferenceChangeListener(darkLightListener);

    }
    private boolean isDarkMode(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getBoolean("dark_mode", false);
    }
}