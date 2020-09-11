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
import com.squareup.picasso.Picasso;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.LoginOrRegister;
import pack.jetminister.ui.activities.ProfileImageActivity;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private AppCompatActivity mContext;

    private ImageView profileImageIV;
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
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        Button testBtn = rootview.findViewById(R.id.btn_test_auth);
        profileImageIV = rootview.findViewById(R.id.iv_profile_image);
        usernameTV = rootview.findViewById(R.id.tv_profile_username);
        descriptionTV = rootview.findViewById(R.id.tv_profile_description);
        startStreamBtn = rootview.findViewById(R.id.btn_start_livestream);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    Toast.makeText(mContext, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    ;
                } else {
                    Toast.makeText(mContext, "NOAUTH", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileImageIV.setOnLongClickListener(new View.OnLongClickListener() {
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
        if (currentUser == null) {
            mContext.finish();
            Intent intent = new Intent(mContext, LoginOrRegister.class);
            startActivity(intent);
        }
    }

    private void updateUI() {
        Context context = usernameTV.getContext();
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