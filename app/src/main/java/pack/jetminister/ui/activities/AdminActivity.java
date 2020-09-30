package pack.jetminister.ui.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.adapter.AdminAdapter;

import static pack.jetminister.data.User.KEY_USERS;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView mRecyclerVew;
    private AdminAdapter mAdapter;

    private List<String> userIDs;

    public AdminActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mRecyclerVew = findViewById(R.id.rv_admin);
        mRecyclerVew.setHasFixedSize(true);
        mRecyclerVew.setLayoutManager(new LinearLayoutManager(this));

        userIDs = new ArrayList<>();
        DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

        usersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String userID = postSnapshot.getKey();
                    userIDs.add(userID);
                }
                mAdapter = new AdminAdapter(AdminActivity.this, userIDs);
                mRecyclerVew.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
