package pack.jetminister.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.adapter.Top100Adapter;

import static pack.jetminister.data.User.KEY_USERS;


public class Top100Fragment extends Fragment {

    private static final String TAG = "Top100Fragment";
    private RecyclerView mRecyclerVew;
    private Top100Adapter mAdapter;
    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mStreamerUsernames;
    private List<String> mFilteredStreamerUsernames;
    private AppCompatActivity mContext;

    public Top100Fragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_top100, container, false);

        mRecyclerVew = rootview.findViewById(R.id.rv_top100);
        mRecyclerVew.setHasFixedSize(true);
        mRecyclerVew.setLayoutManager(new LinearLayoutManager(mContext));

        mAllStreamerIDs = new ArrayList<>();
        mFilteredStreamerIDs = new ArrayList<>();
        mStreamerUsernames = new ArrayList<>();
        mFilteredStreamerUsernames = new ArrayList<>();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAllStreamerIDs.clear();
                mFilteredStreamerIDs.clear();
                mStreamerUsernames.clear();
                mFilteredStreamerUsernames.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    String userID = postSnapshot.getKey();
                    String username = user.getUsername();
                    mAllStreamerIDs.add(userID);
                    mFilteredStreamerIDs.add(userID);
                    mStreamerUsernames.add(username);
                    mFilteredStreamerUsernames.add(username);

                }
                mAdapter = new Top100Adapter(mContext, mAllStreamerIDs, mFilteredStreamerIDs, mStreamerUsernames, mFilteredStreamerUsernames);
                mRecyclerVew.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return rootview;
    }
}