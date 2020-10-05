package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                Toast.makeText(context, R.string.settings_dark_mode_on_message, Toast.LENGTH_SHORT).show();
                darkLight.setChecked(false);
            } else {
                Toast.makeText(context, R.string.settings_dark_mode_off_message, Toast.LENGTH_SHORT).show();
                darkLight.setChecked(true);
            }
            return false;
        }
    };

    public SettingsFragment(){
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        darkLight = findPreference("dark_mode");
        context = darkLight.getContext();
        darkLight.setOnPreferenceChangeListener(darkLightListener);
        //TODO: implement dark mode in other activities by calling SharedPrefs boolean??
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDarkMode = settings.getBoolean("dark_mode", false);
    }
}