package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pack.jetminister.R;
import pack.jetminister.ui.fragments.SettingsFragment;
import pack.jetminister.ui.fragments.LiveFragment;
import pack.jetminister.ui.fragments.ProfileFragment;
import pack.jetminister.ui.fragments.Top100Fragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment destinationFragment;
            //load different fragments when menu item is selected
            switch (item.getItemId()) {
                case R.id.live_fragment:
                    toolbar.setTitle(R.string.fragment_live);
                    destinationFragment = new LiveFragment();
                    loadFragment(destinationFragment);
                    return true;
                case R.id.top100_fragment:
                    toolbar.setTitle(R.string.fragment_top100);
                    destinationFragment = new Top100Fragment();
                    loadFragment(destinationFragment);
                    return true;
                case R.id.profile_fragment:
                    toolbar.setTitle(R.string.fragment_profile);
                    destinationFragment = new ProfileFragment();
                    loadFragment(destinationFragment);
                    return true;

                case R.id.settings_fragment:
                    toolbar.setTitle(R.string.fragment_settings);
                    destinationFragment = new SettingsFragment();
                    loadFragment(destinationFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check if system is set to dark mode,
        // delegate will recreate all activities accordingly
        if(isDarkMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideStatusBar();
        setContentView(R.layout.activity_main);

        //create toolbar to set titles of fragments but do not show it
        toolbar = getSupportActionBar();
        toolbar.hide();

        //create bottom navigation, link to the XML layout and attach listener
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        //set up navigation between the bottom navigation fragments
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    private void loadFragment(Fragment fragment) {
        //navigate to and replace fragments within the bottom navigation, then remove them from back stack
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private boolean isDarkMode(){
        //get the dark mode setting from app's SharedPreferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getBoolean("dark_mode", false);
    }
}