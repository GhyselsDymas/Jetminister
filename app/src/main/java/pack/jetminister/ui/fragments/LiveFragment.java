package pack.jetminister.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.ui.util.adapter.ThemeForLivePageAdapter;

public class LiveFragment extends Fragment {

    private RecyclerView themeRecyclerView;
    private AppCompatActivity mycontext;
    private ThemeForLivePageAdapter mAdapter;

    public LiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mycontext = (AppCompatActivity)context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_live_, container, false);

        themeRecyclerView = rootview.findViewById(R.id.theme_recycler_view);
        themeRecyclerView.setHasFixedSize(true);
        themeRecyclerView.setLayoutManager(new LinearLayoutManager(mycontext));


        String[] myResArray = getResources().getStringArray(R.array.themes);
        List<String> myResArrayList = Arrays.asList(myResArray);

        mAdapter = new ThemeForLivePageAdapter(mycontext, myResArrayList);
        themeRecyclerView.setAdapter(mAdapter);

        return rootview;
    }
}