package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
import pack.jetminister.ui.dialogs.ReportDialog;
import pack.jetminister.ui.dialogs.StreamerProfileErrorDialog;

import static pack.jetminister.data.Report.KEY_REPORT;
import static pack.jetminister.data.User.KEY_DESCRIPTION;
import static pack.jetminister.data.User.KEY_FOLLOWERS;
import static pack.jetminister.data.User.KEY_FOLLOWING;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_STREAMER;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class StreamerProfileActivity extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private TextView usernameTV, descriptionTV, followersTV, followingTV;
    private ImageView profileIV, reportIV, followUnfollowIV;

    private boolean followed;
    private List<Follow> mFollowerIDs;
    private List<Follow> mFollowingIDs;
    private FirebaseAuth  mAuth = FirebaseAuth.getInstance();
    private String currentUserId;
    private String streamerID;

    View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReportDialog newReportDialogDialog = new ReportDialog();

            Bundle bundle = new Bundle();
            bundle.putString(KEY_STREAMER, streamerID);
            newReportDialogDialog.setArguments(bundle);

            newReportDialogDialog.show(getSupportFragmentManager(), KEY_REPORT);
        }
    };

    View.OnClickListener followListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                usersRef.child(currentUserId).child(KEY_USERNAME)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String currentUsername = snapshot.getValue(String.class);
                                    if (!followed) {
                                        Follow newFollower = new Follow(currentUserId, currentUsername);
                                        usersRef.child(streamerID).child(KEY_FOLLOWERS).push().setValue(newFollower);
                                        followed = true;
                                    } else {
                                        usersRef.child(streamerID).child(KEY_FOLLOWERS)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    Follow deleteFollower = dataSnapshot.getValue(Follow.class);
                                                    if (deleteFollower.getFollowID().equals(currentUserId)) {
                                                        String key = dataSnapshot.getKey();
                                                        usersRef.child(streamerID).child(KEY_FOLLOWERS).child(key).removeValue();
                                                        followed = false;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer_profile);
        hideStatusBar();
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }

        if (mAuth.getCurrentUser() != null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        followUnfollowIV = findViewById(R.id.iv_streamer_follow);
        usernameTV = findViewById(R.id.tv_streamer_profile_username);
        descriptionTV = findViewById(R.id.tv_streamer_profile_description);
        followersTV = findViewById(R.id.tv_streamer_profile_followers);
        followingTV = findViewById(R.id.tv_streamer_profile_following);
        profileIV = findViewById(R.id.iv_streamer_profile_image);
        reportIV = findViewById(R.id.iv_report_streamer_profile);

        reportIV.setOnClickListener(reportListener);
        followUnfollowIV.setOnClickListener(followListener);

        getStreamerInfo();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    private void getStreamerInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            streamerID = intent.getStringExtra(KEY_USER_ID);
        }
    }

    private void updateUI(){
        if (streamerID != null) {
            usersRef.child(streamerID).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usernameTV.setText(snapshot.child(KEY_USERNAME).getValue().toString());
                            descriptionTV.setText(snapshot.child(KEY_DESCRIPTION).getValue().toString());
                            String imageURI = snapshot.child(KEY_IMAGE_URL).getValue().toString();
                            Uri thisURI = Uri.parse(imageURI);
                            Picasso.get()
                                    .load(thisURI)
                                    .fit()
                                    .centerCrop()
                                    .into(profileIV);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            getFollowers();
            getFollowing();
            showFollowUnfollowIV();
        } else {
            showStreamerProfileErrorDialog();
        }
    }

    private void getFollowers(){
        mFollowerIDs = new ArrayList<>();
        usersRef.child(streamerID).child(KEY_FOLLOWERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mFollowerIDs.add(snapshot.getValue(Follow.class));
                showFollowersCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mFollowerIDs.remove(snapshot.getValue(Follow.class));
                showFollowersCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowing(){
        mFollowingIDs = new ArrayList<>();
        usersRef.child(streamerID).child(KEY_FOLLOWING).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mFollowingIDs.add(snapshot.getValue(Follow.class));
                showFollowingCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                showFollowingCount();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mFollowingIDs.remove(snapshot.getValue(Follow.class));
                showFollowingCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showFollowersCount(){
        followersTV.setText(mFollowerIDs.size());
    }

    private void showFollowingCount(){
        followingTV.setText(mFollowingIDs.size());
    }

    private void showFollowUnfollowIV(){
        usersRef.child(streamerID).child(KEY_FOLLOWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Follow deleteFollower = dataSnapshot.getValue(Follow.class);
                            if (deleteFollower != null && deleteFollower.getFollowID().equals(currentUserId)) {
                                followed = true;
                                followUnfollowIV.setImageResource(R.drawable.ic_unfollow_24);
                                return;
                            }
                            followed = false;
                            followUnfollowIV.setImageResource(R.drawable.ic_follow_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void showStreamerProfileErrorDialog(){
        StreamerProfileErrorDialog streamerPrifileErrorDialog = new StreamerProfileErrorDialog();
        streamerPrifileErrorDialog.show(getSupportFragmentManager(), "stream_error");
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
