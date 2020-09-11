package pack.jetminister.ui.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PersistableBundle;

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

public class AdminActivity extends AppCompatActivity {

    private RecyclerView mRecyclerVew;
    private AdminAdapter mAdapter;

    private DatabaseReference mDatabaseRef;
    private List<User> mUsers;

    public AdminActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        mRecyclerVew = findViewById(R.id.recycler_admin);
        mRecyclerVew.setHasFixedSize(true);
        mRecyclerVew.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    mUsers.add(user);
                }

                mAdapter = new AdminAdapter(AdminActivity.this, mUsers);
                mRecyclerVew.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
