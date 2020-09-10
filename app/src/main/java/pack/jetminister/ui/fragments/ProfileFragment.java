package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import pack.jetminister.R;
import pack.jetminister.ui.LoginOrRegister;
import pack.jetminister.ui.ProfileImageActivity;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private SharedPreferences mSharedPreferences;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentuser = mAuth.getCurrentUser();
    private AppCompatActivity mContext;

    private ImageView profileImage;
    private TextView usernameTV;
    private TextView descriptionTV;

    private Button startStreamBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, container, false);
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        final String currentUsername = mSharedPreferences.getString(SHARED_PREFS_USERNAME, null);
        final String currentImageURL = mSharedPreferences.getString(SHARED_PREFS_IMAGE_URL, "xXxXx");

//        if (currentUsername == null) {
//            Intent intent = new Intent(mContext, LoginOrRegister.class);
//            startActivity(intent);
//
//        } else {
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        Button testBtn = rootview.findViewById(R.id.btn_test_auth);
        profileImage = rootview.findViewById(R.id.iv_profile_image);
        usernameTV = rootview.findViewById(R.id.tv_profile_username);
        descriptionTV = rootview.findViewById(R.id.tv_profile_description);
        startStreamBtn = rootview.findViewById(R.id.btn_start_livestream);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentuser != null) {
                    Toast.makeText(mContext, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    ;
                } else {
                    Toast.makeText(mContext, "NOAUTH", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openImagePage();
                return true;
            }
        });

        startStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newDialogFragment = new DialogFragment();
                newDialogFragment.show(getParentFragmentManager(), "themes");
            }
        });


        updateUI();
//        }
        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentuser == null) {
            Intent intent = new Intent(mContext, LoginOrRegister.class);
            startActivity(intent);
        }
    }

    private void updateUI() {
        Context context = usernameTV.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        usernameTV.setText(sharedPreferences.getString(SHARED_PREFS_USERNAME, ""));
        descriptionTV.setText(sharedPreferences.getString(SHARED_PREFS_DESCRIPTION, ""));

        Uri myUri = Uri.parse(sharedPreferences.getString(SHARED_PREFS_IMAGE_URL, ""));
//        profileImage.setImageURI(myUri);
        Picasso.get().load(myUri).into(profileImage);
    }

    private void openImagePage() {
        Intent intent = new Intent(mContext, ProfileImageActivity.class);
        startActivity(intent);
    }
}