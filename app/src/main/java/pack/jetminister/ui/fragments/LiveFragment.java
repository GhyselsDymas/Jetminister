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

import static pack.jetminister.data.User.KEY_USERS;

public class LiveFragment extends Fragment {

    private AppCompatActivity mContext;
    private SearchBarAdapter searchFilterAdapter;

    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mAllStreamerNames;
    private List<String> mFilteredStreamerNames;

    private RecyclerView searchFilterRV;

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
        @Override
        public boolean onQueryTextChange(String constraint) {
            //show search results only if something was entered in the search bar
            if(!constraint.isEmpty()){
            searchFilterRV.setVisibility(View.VISIBLE);
                searchFilterAdapter.getFilter().filter(constraint);
            }
            else {
                searchFilterRV.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    };

    public LiveFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_live_, container, false);

        //link search view to the XML and attach listener
        SearchView searchView = rootview.findViewById(R.id.live_search_view);
        searchView.setOnQueryTextListener(searchListener);

        //link theme recycler view to the XML, configure with layout manager
        RecyclerView themeRecyclerView = rootview.findViewById(R.id.rv_live_theme);
        themeRecyclerView.setHasFixedSize(true);
        themeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        //get array of themes from resources and convert to List
        //pass list of themes to theme adapter's constructor
        String[] myResArray = mContext.getResources().getStringArray(R.array.themes);
        List<String> mThemes = Arrays.asList(myResArray);
        LiveThemeAdapter themeAdapter = new LiveThemeAdapter(mContext, mThemes);
        themeRecyclerView.setAdapter(themeAdapter);

        //link search recycler view to the XML, configure with layout manager
        searchFilterRV = rootview.findViewById(R.id.rv_search_bar);
        searchFilterRV.setHasFixedSize(true);
        searchFilterRV.setLayoutManager(new LinearLayoutManager(mContext));

        //initialise lists to be passed to search adapter's constructor after database reference
        mAllStreamerIDs = new ArrayList<>();
        mFilteredStreamerIDs = new ArrayList<>();
        mAllStreamerNames = new ArrayList<>();
        mFilteredStreamerNames = new ArrayList<>();

        //make reference to database and listen for value changes in the "users" node
        FirebaseDatabase.getInstance().getReference(KEY_USERS)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear all lists first to avoid duplicates
                mAllStreamerIDs.clear();
                mFilteredStreamerIDs.clear();
                mAllStreamerNames.clear();
                mFilteredStreamerNames.clear();
                //iterate over all database entries inside node and cast to User object
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    //get the streamers AuthIDs and usernames
                    String streamerID = postSnapshot.getKey();
                    String streamerName = user.getUsername();
                    //add all values to the lists
                    mAllStreamerIDs.add(streamerID);
                    mFilteredStreamerIDs.add(streamerID);
                    mAllStreamerNames.add(streamerName);
                    mFilteredStreamerNames.add(streamerName);
                }
                searchFilterAdapter = new SearchBarAdapter(mContext,
                        mAllStreamerIDs,
                        mFilteredStreamerIDs,
                        mAllStreamerNames,
                        mFilteredStreamerNames
                );
                searchFilterRV.setAdapter(searchFilterAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return rootview;
    }
}