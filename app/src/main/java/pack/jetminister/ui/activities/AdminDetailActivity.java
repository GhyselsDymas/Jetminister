package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Report;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.adapter.ReportAdapter;

import static pack.jetminister.data.User.KEY_DESCRIPTION;
import static pack.jetminister.data.User.KEY_REPORTS_LOGGED;
import static pack.jetminister.data.User.KEY_REPORTS_RECEIVED;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class AdminDetailActivity extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private String detailUserId;
    private List<Report> mReports;

    private TextView usernameTV, descriptionTV, followersTV, followingTV;
    private AppCompatToggleButton reportsBtn;
    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;

    View.OnClickListener descriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usersRef.child(detailUserId).child(KEY_DESCRIPTION)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String detailDescription = snapshot.getValue(String.class);
                                if (!detailDescription.isEmpty()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminDetailActivity.this);
                                    builder.setTitle(KEY_DESCRIPTION.toUpperCase())
                                            .setMessage(detailDescription)
                                            .show();
                                } else {
                                    Toast.makeText(AdminDetailActivity.this, R.string.profile_description_empty, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    };

    CompoundButton.OnCheckedChangeListener reportsListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                loadReportsLogged();
            } else {
                loadReportsReceived();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail);

        usernameTV = findViewById(R.id.detail_tv_username);
        descriptionTV = findViewById(R.id.detail_tv_description);
        followersTV = findViewById(R.id.detail_tv_followers);
        followingTV = findViewById(R.id.detail_tv_following);
        reportsBtn = findViewById(R.id.detail_btn_reports);
        reportsBtn.setOnCheckedChangeListener(reportsListener);
        descriptionTV.setOnClickListener(descriptionListener);
        reportsRecyclerView = findViewById(R.id.detail_rv_reports);
        reportsRecyclerView.setHasFixedSize(true);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReports = new ArrayList<>();

        getUserInfo();
        usersRef.child(detailUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User detailUser = snapshot.getValue(User.class);
                            String detailUsername = detailUser.getUsername();
                            usernameTV.setText(detailUsername);
                            int detailFollowers = 0;
                            if (detailUser.getFollowers() != null) {
                                detailFollowers = detailUser.getFollowers().size();
                            }
                            followersTV.setText(String.valueOf(detailFollowers));
                            int detailFollowing = 0;
                            if (detailUser.getFollowing() != null) {
                                detailFollowing = detailUser.getFollowing().size();
                            }
                            followingTV.setText(String.valueOf(detailFollowing));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        loadReportsReceived();
    }

    private void loadReportsLogged() {
        mReports.clear();
        usersRef.child(detailUserId).child(KEY_REPORTS_LOGGED)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            mReports.add(dataSnapshot.getValue(Report.class));
                        }
                        reportAdapter = new ReportAdapter(AdminDetailActivity.this, mReports);
                        reportsRecyclerView.setAdapter(reportAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadReportsReceived() {
        mReports.clear();
        usersRef.child(detailUserId).child(KEY_REPORTS_RECEIVED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mReports.add(dataSnapshot.getValue(Report.class));
                }
                reportAdapter = new ReportAdapter(AdminDetailActivity.this, mReports);
                reportsRecyclerView.setAdapter(reportAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getUserInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            detailUserId = intent.getStringExtra(KEY_USER_ID);
        }
    }
}