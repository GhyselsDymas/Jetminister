package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pack.jetminister.R;
import pack.jetminister.ui.dialogs.ReportDialog;
import pack.jetminister.ui.dialogs.StreamErrorDialog;
import pack.jetminister.ui.dialogs.StreamerProfileErrorDialog;

import static pack.jetminister.data.User.KEY_DESCRIPTION;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class StreamerProfileActivity extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private TextView usernameTV, descriptionTV, followersTV, followingTV;
    private ImageView profileIV, reportIV;
    private String streamerID;

    View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReportDialog newReportDialogDialog = new ReportDialog();

            Bundle bundle = new Bundle();
            bundle.putString("msg", streamerID);
            newReportDialogDialog.setArguments(bundle);

            newReportDialogDialog.show(getSupportFragmentManager(), "report");
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

        usernameTV = findViewById(R.id.tv_streamer_profile_username);
        descriptionTV = findViewById(R.id.tv_streamer_profile_description);
        followersTV = findViewById(R.id.tv_streamer_profile_followers);
        followingTV = findViewById(R.id.tv_streamer_profile_following);
        profileIV = findViewById(R.id.iv_streamer_profile_image);
        reportIV = findViewById(R.id.iv_report_streamer_profile);
        reportIV.setOnClickListener(reportListener);

        getStreamerInfo();

        if (streamerID != null) {
            usersRef.child(streamerID).
                    addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usernameTV.setText(snapshot.child(KEY_USERNAME).getValue().toString());
                    descriptionTV.setText(snapshot.child(KEY_DESCRIPTION).getValue().toString());
                    //followersTV.setText(snapshot.child(KEY_FOLLOWERS).getValue().toString());
                    //followingTV.setText(snapshot.child(KEY_FOLLOWING).getValue().toString());

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

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    private void getStreamerInfo() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEY_USER_ID)) {
            streamerID = intent.getStringExtra(KEY_USER_ID);
        }
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
