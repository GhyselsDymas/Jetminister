package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.squareup.picasso.Picasso;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.BroadcastActivity;
import pack.jetminister.ui.activities.LoginRegisterActivity;
import pack.jetminister.ui.activities.PermissionsActivity;
import pack.jetminister.ui.activities.ProfileImageActivity;
import pack.jetminister.ui.dialogs.ThemeChooserDialog;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private AppCompatActivity mContext;

    private ImageView profileImageIV;
    private TextView usernameTV;
    private TextView descriptionTV;

    public ProfileFragment() {
    }

    View.OnClickListener startStreamListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            ThemeChooserDialog newThemeChooserDialog = new ThemeChooserDialog();
//            newThemeChooserDialog.show(getParentFragmentManager(), "themes");
            Intent intent = new Intent(mContext, PermissionsActivity.class);
            startActivity(intent);
        }
    };

    View.OnLongClickListener profileImageListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            openImagePage();
            return true;
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTV = rootView.findViewById(R.id.tv_profile_username);
        descriptionTV = rootView.findViewById(R.id.tv_profile_description);
        profileImageIV = rootView.findViewById(R.id.iv_profile_image);
        Button startStreamBtn = rootView.findViewById(R.id.btn_start_livestream);

        profileImageIV.setOnLongClickListener(profileImageListener);
        startStreamBtn.setOnClickListener(startStreamListener);

        updateUI();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            mContext.finish();
            Intent intent = new Intent(mContext, LoginRegisterActivity.class);
            startActivity(intent);
        }
    }

    private void updateUI() {
        if (currentUser != null) {
            String uID = currentUser.getUid();
            usersRef.child(uID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User currentUser = snapshot.getValue(User.class);
                        usernameTV.setText(currentUser.getUsername());
                        descriptionTV.setText(currentUser.getDescription());
                        if (!currentUser.getImageURL().isEmpty()) {
                            Picasso.get()
                                    .load(currentUser.getImageURL())
                                    .fit()
                                    .centerCrop()
                                    .into(profileImageIV);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void openImagePage() {
        Intent intent = new Intent(mContext, ProfileImageActivity.class);
        startActivity(intent);
    }
}