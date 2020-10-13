package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.AdminDetailActivity;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_ID;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.LiveStream.KEY_STREAM_THEME;
import static pack.jetminister.data.LiveStream.KEY_STREAM_USERNAME;
import static pack.jetminister.data.User.KEY_LOCATION;
import static pack.jetminister.data.User.KEY_STREAMER;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;
import static pack.jetminister.data.util.SourceConnectionInformation.KEY_STREAM_PUBLISH_URL;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminHolder> {

    private Context mContext;
    private List<String> mUserIDs;

    public AdminAdapter(Context context, List<String> IDs){
        mContext = context;
        mUserIDs = IDs;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin, parent, false);
        return new AdminHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.AdminHolder holder, int position) {
        String currentUserID = mUserIDs.get(position);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
        DatabaseReference streamsRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
        usersRef.child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User currentUser = snapshot.getValue(User.class);
                            if (currentUser != null) {
                                holder.usernameAdmin.setText(currentUser.getUsername());
                                if (!currentUser.isStreamer()){
                                    holder.streamerSwitch.setChecked(false);
                                } else {
                                    holder.streamerSwitch.setChecked(true);
                                }
                                holder.streamerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            currentUser.setStreamer(true);
                                            usersRef.child(currentUserID).child(KEY_STREAMER).setValue(true);
                                        } else {
                                            currentUser.setStreamer(false);
                                            usersRef.child(currentUserID).child(KEY_STREAMER).setValue(false);
                                            streamsRef.child(currentUserID).removeValue();
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
        return mUserIDs.size();
    }

    public class AdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameAdmin;
        public SwitchCompat streamerSwitch;
        public MaterialButton detailBtn;

        public AdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameAdmin = itemView.findViewById(R.id.Textview_admin);
            streamerSwitch = itemView.findViewById(R.id.switch_streamer_admin_page);
            detailBtn = itemView.findViewById(R.id.btn_more_info_user);

            detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String currentUserId = mUserIDs.get(position);
                    Intent intent = new Intent(mContext, AdminDetailActivity.class);
                    intent.putExtra(KEY_USER_ID, currentUserId);
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
