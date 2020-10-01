package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pack.jetminister.R;
import pack.jetminister.ui.dialogs.DescriptionChangeDialog;
import pack.jetminister.ui.dialogs.ReportDialog;

import static pack.jetminister.data.User.KEY_DESCRIPTION;
import static pack.jetminister.data.User.KEY_FOLLOWERS;
import static pack.jetminister.data.User.KEY_FOLLOWING;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class StreamerProfileActivity extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private TextView usernameTV , descriptionTV , followersTV , followingTV;
    private ImageView profileIV , reportIV;

    private String streamerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer_profile);

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

        usersRef.child(streamerID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usernameTV.setText(snapshot.child(KEY_USERNAME).getValue().toString());
                    descriptionTV.setText(snapshot.child(KEY_DESCRIPTION).getValue().toString());
                    //followersTV.setText(snapshot.child(KEY_FOLLOWERS).getValue().toString());
                    //followingTV.setText(snapshot.child(KEY_FOLLOWING).getValue().toString());


                    String imageURI =  snapshot.child(KEY_IMAGE_URL).getValue().toString();
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
    }

    View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ReportDialog newReportDialogDialog = new ReportDialog();
            newReportDialogDialog.show(getSupportFragmentManager(), "report");
        }
    };

    private void getStreamerInfo(){
        Bundle extra = getIntent().getExtras();
        streamerID = extra.getString(KEY_USER_ID);
    }
}