package pack.jetminister.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.fragments.SettingsFragment;
import pack.jetminister.ui.fragments.LiveFragment;
import pack.jetminister.ui.fragments.ProfileFragment;
import pack.jetminister.ui.fragments.Top100Fragment;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;
    private ProfileFragment profileFragment;
    private Top100Fragment top100Fragment;
    private LiveFragment liveFragment;
    private User authenticatedUser;
    private static final String BUNDLE_KEY_AUTHENTICATED_USER = "authenticated_user";
    private static final String TAG = "MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment destinationFragment;
            Bundle args = new Bundle();
            args.putSerializable(BUNDLE_KEY_AUTHENTICATED_USER, authenticatedUser);
            //load different fragments when menu item is selected
            switch (item.getItemId()) {
                case R.id.live_fragment:
                    toolbar.setTitle(R.string.page_start);
                    destinationFragment = new LiveFragment();
                    loadFragment(destinationFragment, args);
                    return true;
                case R.id.top100_fragment:
                    toolbar.setTitle(R.string.page_top100);
                    destinationFragment = new Top100Fragment();
                    loadFragment(destinationFragment, args);
                    return true;
                case R.id.profile_fragment:
                    toolbar.setTitle(R.string.page_profile);
                    destinationFragment = new ProfileFragment();
                    loadFragment(destinationFragment, args);
                    return true;
                case R.id.settings_fragment:
                    toolbar.setTitle(R.string.page_settings);
                    destinationFragment = new SettingsFragment();
                    loadFragment(destinationFragment, args);
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

        Button registerButton = findViewById(R.id.btn_registration);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });
        Button testAuthenticatedUserButton = findViewById(R.id.btn_testauthenticateduser);
        testAuthenticatedUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, authenticatedUser.toString());
            }
        });

    }

    private User receiveUserData() {
        Intent intent = getIntent();
        User user = new User();
        if (intent != null) {
            String usernameFromDatabase = intent.getStringExtra("username");
            String passwordFromDatabase = intent.getStringExtra("password");
            String emailFromDatabase = intent.getStringExtra("email");
            String descriptionFromDatabase = intent.getStringExtra("description");
            String themeFromDatabase = intent.getStringExtra("theme");
            String imageURLFromDatabase = intent.getStringExtra("imageURL");
            boolean streamerFromDatabaseDatabase = intent.getBooleanExtra("streamer", false);

            user.setUsername(usernameFromDatabase);
            user.setPassword(passwordFromDatabase);
            user.setEmail(emailFromDatabase);
            user.setDescription(descriptionFromDatabase);
            user.setTheme(themeFromDatabase);
            user.setImageURL(imageURLFromDatabase);
            user.setStreamer(streamerFromDatabaseDatabase);
        }
        return user;
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginOrRegister.class);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment, Bundle args) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment.getClass(), args);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}