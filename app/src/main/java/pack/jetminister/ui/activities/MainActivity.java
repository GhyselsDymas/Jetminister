package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.fragments.SettingsFragment;
import pack.jetminister.ui.fragments.LiveFragment;
import pack.jetminister.ui.fragments.ProfileFragment;
import pack.jetminister.ui.fragments.Top100Fragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_EMAIL = "email";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_THEME = "theme";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private static final String SHARED_PREFS_STREAMER = "streamer";

    private ActionBar toolbar;


    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment destinationFragment;
            //load different fragments when menu item is selected
            switch (item.getItemId()) {
                case R.id.live_fragment:
                    toolbar.setTitle(R.string.page_start);
                    destinationFragment = new LiveFragment();
                    loadFragment(destinationFragment);
                    return true;
                case R.id.top100_fragment:
                    toolbar.setTitle(R.string.page_top100);
                    destinationFragment = new Top100Fragment();
                    loadFragment(destinationFragment);
                    return true;
                case R.id.profile_fragment:
                    toolbar.setTitle(R.string.page_profile);
                    destinationFragment = new ProfileFragment();
                    loadFragment(destinationFragment);
                    return true;

                case R.id.settings_fragment:
                    toolbar.setTitle(R.string.page_settings);
                    destinationFragment = new SettingsFragment();
                    loadFragment(destinationFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final User authenticatedUser = receiveUserData();

        toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.page_start);
        toolbar.hide();

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

 //        loadFragment(new LiveFragment());

        Button buttonTest = findViewById(R.id.testprefs);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences myPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                Toast.makeText(MainActivity.this, myPrefs.getString(SHARED_PREFS_IMAGE_URL, "xx"), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private User receiveUserData() {
        User authenticatedUser = new User();
        Intent intent = getIntent();
        if (intent != null) {
            String usernameFromDatabase = intent.getStringExtra("username");
            String passwordFromDatabase = intent.getStringExtra("password");
            String emailFromDatabase = intent.getStringExtra("email");
            String descriptionFromDatabase = intent.getStringExtra("description");
            String themeFromDatabase = intent.getStringExtra("theme");
            String imageURLFromDatabase = intent.getStringExtra("imageURL");
            boolean streamerFromDatabaseDatabase = intent.getBooleanExtra("streamer", false);

            authenticatedUser.setUsername(usernameFromDatabase);
            authenticatedUser.setPassword(passwordFromDatabase);
            authenticatedUser.setEmail(emailFromDatabase);
            authenticatedUser.setDescription(descriptionFromDatabase);
            authenticatedUser.setTheme(themeFromDatabase);
            authenticatedUser.setImageURL(imageURLFromDatabase);
            authenticatedUser.setStreamer(streamerFromDatabaseDatabase);
        }
        return authenticatedUser;
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginOrRegister.class);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}