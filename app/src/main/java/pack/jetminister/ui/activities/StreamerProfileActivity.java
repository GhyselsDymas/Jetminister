package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import static pack.jetminister.data.User.KEY_STREAMER_ID;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class StreamerProfileActivity extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private static final String TAG = "StreamerProfileActivity";
    private static final String TAG_UNFOLLOW = "unfollow";
    private static final String TAG_FOLLOW = "follow";


    private TextView usernameTV, descriptionTV, followersTV, followingTV;
    private ImageView profileIV, reportIV, followUnfollowIV;


    private List<Follow> mFollowers;
    private List<Follow> mFollowings;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUserId;
    private String streamerID;
    private String streamerUsername;

    View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReportDialog newReportDialogDialog = new ReportDialog();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_USER_ID, currentUserId);
            bundle.putString(KEY_STREAMER_ID, streamerID);
            newReportDialogDialog.setArguments(bundle);
            newReportDialogDialog.show(getSupportFragmentManager(), KEY_REPORT);
        }
    };

    View.OnClickListener followListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (String.valueOf(followUnfollowIV.getTag(R.layout.activity_streamer_profile)).equals(TAG_UNFOLLOW)) {
                deleteFollowerFromStreamer();
                deleteFollowingFromCurrentUser();
                followUnfollowIV.setImageResource(R.drawable.ic_follow_24);
                followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_FOLLOW);
            } else {
                addFollowerToStreamer();
                addFollowingToCurrentUser();
                followUnfollowIV.setImageResource(R.drawable.ic_unfollow_24);
                followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_UNFOLLOW);
            }
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

        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        mFollowers = new ArrayList<>();
        mFollowings = new ArrayList<>();

        followUnfollowIV = findViewById(R.id.iv_streamer_follow);
        usernameTV = findViewById(R.id.tv_streamer_profile_username);
        descriptionTV = findViewById(R.id.tv_streamer_profile_description);
        followersTV = findViewById(R.id.tv_streamer_profile_followers);
        followingTV = findViewById(R.id.tv_streamer_profile_following);
        profileIV = findViewById(R.id.iv_streamer_profile_image);
        reportIV = findViewById(R.id.iv_report_streamer_profile);

        reportIV.setOnClickListener(reportListener);
        followUnfollowIV.setOnClickListener(followListener);
        followUnfollowIV.setImageResource(R.drawable.ic_follow_24);
        followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_FOLLOW);

        getStreamerInfo();
        updateFollowUnfollowIV();
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
            streamerUsername = intent.getStringExtra((KEY_USERNAME));
        }
    }

    private void updateFollowUnfollowIV() {
        usersRef.child(streamerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(KEY_FOLLOWERS)) {
                            for (DataSnapshot dataSnapshot : snapshot.child(KEY_FOLLOWERS).getChildren()) {
                                Follow checkFollower = dataSnapshot.getValue(Follow.class);
                                String followerID = checkFollower.getFollowID();
                                if (followerID.equals(currentUserId)) {
                                    followUnfollowIV.setImageResource(R.drawable.ic_unfollow_24);
                                    followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_UNFOLLOW);
                                    break;
                                } else {
                                    followUnfollowIV.setImageResource(R.drawable.ic_follow_24);
                                    followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_FOLLOW);
                                }
                            }
                        } else {
                            followUnfollowIV.setImageResource(R.drawable.ic_follow_24);
                            followUnfollowIV.setTag(R.layout.activity_streamer_profile, TAG_FOLLOW);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateUI() {
        if (streamerID != null) {
            updateFollowUnfollowIV();
            usersRef.child(streamerID).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                updateFollowerCount();
                                updateFollowingCount();
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
        } else {
            showStreamerProfileErrorDialog();
        }
    }

    private void updateFollowerCount() {
        usersRef.child(streamerID).child(KEY_FOLLOWERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int followersCount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        followersCount++;
                    }
                    followersTV.setText(String.valueOf(followersCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateFollowingCount() {
        usersRef.child(streamerID).child(KEY_FOLLOWING).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int followingCount = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        followingCount++;
                    }
                    followingTV.setText(String.valueOf(followingCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deleteFollowerFromStreamer() {
        usersRef.child(streamerID).child(KEY_FOLLOWERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Follow deleteFollow = dataSnapshot.getValue(Follow.class);
                    if (deleteFollow.getFollowID().equals(currentUserId)) {
                        String followKey = dataSnapshot.getKey();
                        usersRef.child(streamerID).child(KEY_FOLLOWERS).child(followKey).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deleteFollowingFromCurrentUser() {
        usersRef.child(currentUserId).child(KEY_FOLLOWING).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Follow deleteFollow = dataSnapshot.getValue(Follow.class);
                    if (deleteFollow.getFollowID().equals(streamerID)) {
                        String deleteFollowingKey = dataSnapshot.getKey();
                        usersRef.child(currentUserId).child(KEY_FOLLOWING).child(deleteFollowingKey).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addFollowerToStreamer() {
        usersRef.child(currentUserId).child(KEY_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUsername = snapshot.getValue(String.class);
                usersRef.child(streamerID).child(KEY_FOLLOWERS).push().setValue(new Follow(currentUserId, currentUsername));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addFollowingToCurrentUser() {
        usersRef.child(currentUserId).child(KEY_FOLLOWING).push().setValue(new Follow(streamerID, streamerUsername));
    }

    private void showStreamerProfileErrorDialog() {
        StreamerProfileErrorDialog streamerPrifileErrorDialog = new StreamerProfileErrorDialog();
        streamerPrifileErrorDialog.show(getSupportFragmentManager(), "stream_error");
    }

    private void redirectToLogin() {
        finish();
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
