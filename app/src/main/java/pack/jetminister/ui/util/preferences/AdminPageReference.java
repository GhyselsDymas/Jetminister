package pack.jetminister.ui.util.preferences;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import android.util.AttributeSet;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pack.jetminister.R;
import pack.jetminister.ui.activities.AdminActivity;

public class AdminPageReference extends Preference {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");


    public AdminPageReference(Context context, AttributeSet attrs) {
        super(context, attrs);
        //set the right XML file
        setWidgetLayoutResource(R.layout.preference_admin_page);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if (currentUser != null) {
            final String uID = currentUser.getUid();
            final DatabaseReference currentUserDatabaseRef = usersDatabaseRef.child(uID);

            currentUserDatabaseRef.child("username")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists() && snapshot.getValue().toString().equals("Hackermann")) {
                                View logoutIV = holder.findViewById(R.id.admin_button);
                                logoutIV.setClickable(true);
                                logoutIV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent();
                                        intent.setClass(getContext(), AdminActivity.class);
                                        getContext().startActivity(intent);
                                    }
                                });
                            } else {
                                View logoutIV = holder.findViewById(R.id.admin_button);
                                holder.itemView.setVisibility(View.INVISIBLE);
                                logoutIV.setClickable(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            View logoutIV = holder.findViewById(R.id.admin_button);
            holder.itemView.setVisibility(View.INVISIBLE);
            logoutIV.setClickable(false);
        }
    }
}