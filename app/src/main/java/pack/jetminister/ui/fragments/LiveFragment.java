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
import pack.jetminister.ui.util.adapter.LiveThemeAdapter;

public class LiveFragment extends Fragment {

    private AppCompatActivity mContext;
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


        String[] myResArray = mContext.getResources().getStringArray(R.array.themes);
        List<String> mThemes = Arrays.asList(myResArray);
        RecyclerView themeRecyclerView = rootview.findViewById(R.id.rv_live_theme);
        themeRecyclerView.setHasFixedSize(true);
        themeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        LiveThemeAdapter livePageAdapter = new LiveThemeAdapter(mContext, mThemes);
        themeRecyclerView.setAdapter(livePageAdapter);

        return rootview;
    }
}