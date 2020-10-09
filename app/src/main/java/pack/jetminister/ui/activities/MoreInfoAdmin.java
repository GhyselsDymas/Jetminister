package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
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
import pack.jetminister.ui.util.adapter.LiveThemeAdapter;
import pack.jetminister.ui.util.adapter.ReportAdapter;

import static pack.jetminister.data.User.KEY_REPORTS_LOGGED;
import static pack.jetminister.data.User.KEY_REPORTS_RECEIVED;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class MoreInfoAdmin extends AppCompatActivity {

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private String detailUserId;
    private List<Report> mReports;


    private TextView usernameTV, descroptionTV, followersTV, followingTV;
    private ToggleButton reportsBtn;
    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;

    CompoundButton.OnCheckedChangeListener reportsListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isChecked()){
                loadReportsLogged();
                buttonView.setChecked(true);
            } else {
                loadReportsReceived();
                buttonView.setChecked(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_admin);

        usernameTV = findViewById(R.id.detail_tv_username);
        descroptionTV = findViewById(R.id.detail_tv_description);
        followersTV = findViewById(R.id.detail_tv_followers);
        followingTV = findViewById(R.id.detail_tv_following);
        reportsBtn = findViewById(R.id.detail_btn_reports);
        reportsBtn.setOnCheckedChangeListener(reportsListener);
        getUserInfo();

        usersRef.child(detailUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User detailUser = snapshot.getValue(User.class);
                        String detailUsername = detailUser.getUsername();
                        usernameTV.setText(detailUsername);
                        int detailFollowers = detailUser.getFollowers().size();
                        followersTV.setText(String.valueOf(detailFollowers));
                        int detailFollowing = detailUser.getFollowing().size();
                        followingTV.setText(String.valueOf(detailFollowing));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        reportsRecyclerView = findViewById(R.id.detail_rv_reports);
        reportsRecyclerView.setHasFixedSize(true);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReports = new ArrayList<>();
        if (reportsBtn.isChecked()) {
            loadReportsLogged();
        } else {
            loadReportsReceived();
        }
    }

    private void loadReportsLogged(){
        mReports.clear();
        usersRef.child(detailUserId).child(KEY_REPORTS_LOGGED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mReports.add(snapshot.getValue(Report.class));
                }
                reportAdapter = new ReportAdapter(MoreInfoAdmin.this, mReports);
                reportsRecyclerView.setAdapter(reportAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void loadReportsReceived(){
        mReports.clear();
        usersRef.child(detailUserId).child(KEY_REPORTS_RECEIVED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mReports.add(snapshot.getValue(Report.class));
                }
                reportAdapter = new ReportAdapter(MoreInfoAdmin.this, mReports);
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