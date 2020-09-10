package pack.jetminister.ui.util.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pack.jetminister.R;
import pack.jetminister.ui.MainActivity;

public class LogOutPreference extends Preference {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";

    public LogOutPreference(Context context, AttributeSet attrs) {
        super(context, attrs );
        //set the right XML file
        setWidgetLayoutResource(R.layout.preference_logout);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);


        holder.itemView.setClickable(false);
        View logoutIV = holder.findViewById(R.id.iv_settings_logout);
        if (currentUser != null) {
            logoutIV.setClickable(true);
            logoutIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.clear().apply();
                    mAuth.signOut();

                }
            });
        } else {
            logoutIV.setClickable(false);
        }
    }


}
