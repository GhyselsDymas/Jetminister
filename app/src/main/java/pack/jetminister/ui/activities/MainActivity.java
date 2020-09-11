package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.fragment_live);
        toolbar.hide();

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

//        Button buttonTest = findViewById(R.id.btn_test);
//        buttonTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                    Toast.makeText(MainActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
//                    ;
//                } else {
//                    Toast.makeText(MainActivity.this, "NOAUTH", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}