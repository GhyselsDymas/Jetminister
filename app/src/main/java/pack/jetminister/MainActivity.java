package pack.jetminister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pack.jetminister.tabmenu.LivePage;
import pack.jetminister.tabmenu.ProfilePage;
import pack.jetminister.tabmenu.Top100Page;

public class MainActivity extends AppCompatActivity {
    private ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()){
                case R.id.bottom_nav_start:
                    toolbar.setTitle(R.string.page_start);
                    fragment = new LivePage();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_top100:
                    toolbar.setTitle(R.string.page_top100);
                    fragment = new Top100Page();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_profile:
                    toolbar.setTitle(R.string.page_profile);
                    fragment = new ProfilePage();
                    loadFragment(fragment);
                    return true;
                case R.id.bottom_nav_settings:
                    toolbar.setTitle(R.string.page_settings);
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

        //set menu item title and fragment to 'Start'
        toolbar.setTitle(R.string.page_start);
        loadFragment(new LivePage());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(bottomNavListener);


    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}