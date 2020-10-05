package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

import static pack.jetminister.data.User.KEY_STREAMER;
import static pack.jetminister.data.User.KEY_USERS;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminHolder> {

    private Context mContext;
    private List<String> userIDs;

    public AdminAdapter(Context context, List<String> IDs){
        mContext = context;
        userIDs = IDs;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin, parent, false);
        return new AdminHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.AdminHolder holder, int position) {
        String currentUserID = userIDs.get(position);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
        usersRef.child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User currentUser = snapshot.getValue(User.class);
                            if (currentUser != null) {
                                holder.usernameAdmin.setText(currentUser.getUsername());
                                holder.streamerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            currentUser.setStreamer(true);
                                            usersRef.child(currentUserID).child(KEY_STREAMER).setValue(true);
                                        } else {
                                            currentUser.setStreamer(false);
                                            usersRef.child(currentUserID).child(KEY_STREAMER).setValue(false);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return userIDs.size();
    }

    public class AdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameAdmin;
        public SwitchCompat streamerSwitch;

        public AdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameAdmin = itemView.findViewById(R.id.Textview_admin);
            streamerSwitch = itemView.findViewById(R.id.switch_streamer_admin_page);
        }
    }

}
