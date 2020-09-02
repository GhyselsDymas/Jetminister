package pack.jetminister.ui.tabmenu;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import pack.jetminister.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    public SettingsFragment(){}

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
    }

    //add Dark Mode functionality
}
