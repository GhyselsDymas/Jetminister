package pack.jetminister.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import pack.jetminister.R;
import pack.jetminister.ui.LoginOrRegister;
import pack.jetminister.ui.ProfileImagePage;

public class ProfileFragment extends Fragment {

    private AppCompatActivity mycontext;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mycontext = (AppCompatActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview =  inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView buttonProfile = rootview.findViewById(R.id.imgTourismHr);
        buttonProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openImagePage();
                return true;
            }
        });

        return rootview;
    }

    private void openImagePage() {
        Intent intent = new Intent(mycontext , ProfileImagePage.class);
        startActivity(intent);
    }
}