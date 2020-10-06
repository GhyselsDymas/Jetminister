package pack.jetminister.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.adapter.LiveThemeAdapter;
import pack.jetminister.ui.util.adapter.SearchBarAdapter;

public class LiveFragment extends Fragment {

    private AppCompatActivity mContext;
    private LiveThemeAdapter themeAdapter;
    private SearchBarAdapter searchBarAdapter;
//    private List<User> mUsers;
    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mAllStreamerNames;
    private List<String> mFilteredStreamerNames;
    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String constraint) {

            if(!constraint.isEmpty()){
            searchBarAdapter.getFilter().filter(constraint);
            return false;}
            else {

            }
        }
    };

    public LiveFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_live_, container, false);

        SearchView searchView = rootview.findViewById(R.id.live_search_view);
        searchView.setOnQueryTextListener(searchListener);

        String[] myResArray = mContext.getResources().getStringArray(R.array.themes);
        List<String> mThemes = Arrays.asList(myResArray);
        RecyclerView themeRecyclerView = rootview.findViewById(R.id.rv_live_theme);
        themeRecyclerView.setHasFixedSize(true);
        themeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        themeAdapter = new LiveThemeAdapter(mContext, mThemes);
        themeRecyclerView.setAdapter(themeAdapter);

        RecyclerView SearchBarRecyclerView = rootview.findViewById(R.id.rv_search_bar);
        SearchBarRecyclerView.setHasFixedSize(true);
        SearchBarRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

//        mUsers = new ArrayList<>();
        mAllStreamerIDs =new ArrayList<>();
        mFilteredStreamerIDs =new ArrayList<>();
        mAllStreamerNames=new ArrayList<>();
        mFilteredStreamerNames=new ArrayList<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAllStreamerIDs.clear();
                mFilteredStreamerIDs.clear();
                mAllStreamerNames.clear();
                mFilteredStreamerNames.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    String streamerID = postSnapshot.getKey();
                    String streamerName = user.getUsername();
                    mAllStreamerIDs.add(streamerID);
                    mFilteredStreamerIDs.add(streamerID);
                    mAllStreamerNames.add(streamerName);
                    mFilteredStreamerNames.add(streamerName);
//                    mUsers.add(user);
                }
                searchBarAdapter = new SearchBarAdapter(mContext,
                        mAllStreamerIDs,
                        mFilteredStreamerIDs,
                        mAllStreamerNames,
                        mFilteredStreamerNames
//                        mUsers
                );
                SearchBarRecyclerView.setAdapter(searchBarAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return rootview;
    }
}