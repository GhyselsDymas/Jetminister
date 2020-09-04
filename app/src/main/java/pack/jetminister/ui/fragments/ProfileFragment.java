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
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.ProfileImageActivity;

public class ProfileFragment extends Fragment {

    private AppCompatActivity mycontext;
    private Bundle dataFromUser;
    private User authenticatedUser;

    private ImageView profileImage;
    private TextView usernameTV;

    private TextView descriptionTV;

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
        profileImage = rootview.findViewById(R.id.iv_profile_image);
        usernameTV = rootview.findViewById(R.id.tv_profile_username);
        descriptionTV = rootview.findViewById(R.id.tv_profile_description);

        if (dataPassed()){
            authenticatedUser = (User) dataFromUser.getSerializable("authenticated_user");
            updateUI(authenticatedUser.getUsername(), authenticatedUser.getDescription());
        }

        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                openImagePage();
                return true;
            }
        });

        return rootview;
    }

    private void updateUI(String username, String description) {
        usernameTV.setText(username);
        descriptionTV.setText(description);
    }

    private void openImagePage() {
        Intent intent = new Intent(mycontext , ProfileImageActivity.class);
        startActivity(intent);
    }

    public boolean dataPassed() {
        dataFromUser = getArguments();
        if (dataFromUser != null) {
            if (dataFromUser.containsKey("authenticated_user")) {
                return true;
            }
        }
        return false;
    }
}