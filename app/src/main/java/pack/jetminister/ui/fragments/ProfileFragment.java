package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.LoginOrRegister;
import pack.jetminister.ui.MainActivity;
import pack.jetminister.ui.ProfileImageActivity;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_PASSWORD = "password";
    private static final String SHARED_PREFS_EMAIL = "email";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_THEME = "theme";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private static final String SHARED_PREFS_STREAMER = "streamer";

    private static final String BUNDLE_KEY_AUTHENTICATED_USER = "authenticated_user";
    private AppCompatActivity mycontext;
    private Bundle dataFromUser;
    private User authenticatedUser;

    private ImageView profileImage;
    private TextView usernameTV;
    private TextView descriptionTV;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User user, String key){
        ProfileFragment mFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("authenticated_user", user);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mycontext = (AppCompatActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        SharedPreferences myPrefs = mycontext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        String username = myPrefs.getString(SHARED_PREFS_USERNAME, null);
//        if (username == null) {
//            Intent intent = new Intent(mycontext, LoginOrRegister.class);
//            startActivity(intent);
//
//        } else {
            profileImage = rootview.findViewById(R.id.iv_profile_image);
            usernameTV = rootview.findViewById(R.id.tv_profile_username);
            descriptionTV = rootview.findViewById(R.id.tv_profile_description);

            profileImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    openImagePage();
                    return true;
                }
            });

            updateUI();
            return rootview;
        }
//        return rootview;
//    }


        private void updateUI () {
            Context context = usernameTV.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            usernameTV.setText(sharedPreferences.getString(SHARED_PREFS_USERNAME, ""));
            descriptionTV.setText(sharedPreferences.getString(SHARED_PREFS_DESCRIPTION, ""));
        }

        private void openImagePage () {
            Intent intent = new Intent(mycontext, ProfileImageActivity.class);
            startActivity(intent);
        }

        public boolean dataPassed () {
            dataFromUser = getArguments();
            if (dataFromUser != null) {
                if (dataFromUser.containsKey("authenticated_user")) {
                    Log.d(TAG, "data contains key");
                    return true;
                } else if (dataFromUser.isEmpty()) {
                    Log.d(TAG, "bundle passed but is empty" + dataFromUser.toString());
                }
            }
            return false;
        }

}