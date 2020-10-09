package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Follow;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.AskToStreamActivity;
import pack.jetminister.ui.activities.LoginRegisterActivity;
import pack.jetminister.ui.activities.ProfileImageActivity;
import pack.jetminister.ui.dialogs.DescriptionChangeDialog;
import pack.jetminister.ui.dialogs.ThemeChooserDialog;

import static pack.jetminister.data.User.KEY_FOLLOWERS;
import static pack.jetminister.data.User.KEY_FOLLOWING;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String currentUserID;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private AppCompatActivity mContext;

    private List<Follow> mFollowerIDs;
    private List<Follow> mFollowingIDs;

    private ImageView profileImageIV, descriptionIV;
    private TextView usernameTV, descriptionTV, followersTV, followingTV;

    public ProfileFragment() {
    }

    View.OnClickListener startStreamListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.child(currentUser.getUid()).child("streamer").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            boolean streamer = snapshot.getValue(Boolean.class);
                            if (!streamer){
                                Intent intent = new Intent(mContext, AskToStreamActivity.class);
                                startActivity(intent);
                            }else{
                                ThemeChooserDialog newThemeChooserDialog = new ThemeChooserDialog();
                                newThemeChooserDialog.show(getParentFragmentManager(), "themes");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
    };

    View.OnLongClickListener profileImageListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            openImagePage();
            return true;
        }
    };

    View.OnClickListener descriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DescriptionChangeDialog newDescriptionChangeDialog = new DescriptionChangeDialog();
            newDescriptionChangeDialog.show(getParentFragmentManager(), "themes");
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
        if (currentUser != null) {
            currentUserID = currentUser.getUid();

            usernameTV = rootView.findViewById(R.id.tv_user_profile_username);
            descriptionTV = rootView.findViewById(R.id.tv_user_profile_description);
            followersTV = rootView.findViewById(R.id.tv_user_profile_followers);
            followingTV = rootView.findViewById(R.id.tv_user_profile_following);
            profileImageIV = rootView.findViewById(R.id.iv_user_profile_image);
            Button startStreamBtn = rootView.findViewById(R.id.btn_start_livestream);
            descriptionIV = rootView.findViewById(R.id.iv_user_profile_edit);

            profileImageIV.setOnLongClickListener(profileImageListener);
            startStreamBtn.setOnClickListener(startStreamListener);
            descriptionIV.setOnClickListener(descriptionListener);

            updateUI();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            redirectToLogin();
        }
    }

    private void updateUI() {
            usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
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
            getFollowers();
            getFollowing();
    }

    private void getFollowers() {
        followersTV.setText("0");
        mFollowerIDs = new ArrayList<>();;
        usersRef.child(currentUserID).child(KEY_FOLLOWERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mFollowerIDs.add(snapshot.getValue(Follow.class));
                updateFollowersCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mFollowerIDs.remove(snapshot.getValue(Follow.class));
                updateFollowersCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowing() {
        followingTV.setText("0");
        mFollowingIDs = new ArrayList<>();
        usersRef.child(currentUserID).child(KEY_FOLLOWING).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mFollowingIDs.add(snapshot.getValue(Follow.class));
                updateFollowingCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mFollowingIDs.remove(snapshot.getValue(Follow.class));
                updateFollowingCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateFollowersCount(){
        if (mFollowerIDs != null) {
            followersTV.setText(String.valueOf(mFollowerIDs.size()));
        }
        followersTV.setText("0");
    }

    private void updateFollowingCount() {
        if (mFollowingIDs != null) {
            followingTV.setText(String.valueOf(mFollowingIDs.size()));
        }
    }

    private void openImagePage() {
        Intent intent = new Intent(mContext, ProfileImageActivity.class);
        startActivity(intent);
    }

    private void redirectToLogin(){
        mContext.finish();
        Intent intent = new Intent(mContext, LoginRegisterActivity.class);
        startActivity(intent);
    }
}