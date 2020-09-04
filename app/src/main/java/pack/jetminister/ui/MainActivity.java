package pack.jetminister.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
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

    private User user = new User();

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            //load different fragments when menu item is selected
            switch (item.getItemId()){
                case R.id.bottom_nav_start:
                    toolbar.setTitle(R.string.page_start);
                    fragment = new LiveFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_top100:
                    toolbar.setTitle(R.string.page_top100);
                    fragment = new Top100Fragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_profile:
                    toolbar.setTitle(R.string.page_profile);
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_settings:
                    toolbar.setTitle(R.string.page_settings);
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
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

        toolbar = getSupportActionBar();

        //set menu item title and fragment to 'Start'
        toolbar.setTitle(R.string.page_start);

        toolbar.hide();

        loadFragment(new LiveFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(bottomNavListener);
        Button button = findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });

        receiveUserData();


    }
    private void receiveUserData() {
        Intent intent = getIntent();
        if (intent != null){
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

    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginOrRegister.class);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}