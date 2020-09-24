package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.streamaxia.player.StreamaxiaPlayer;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.LivePlayerActivity;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.User.KEY_USERNAME;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder> {

//    public static final String KEY_LIKES = "likes";
    public static final String KEY_URI = "uri";
    public static final String KEY_TYPE = "type";
    public static final int STREAM_TYPE = StreamaxiaPlayer.TYPE_HLS;

    private Context mContext;
    private List<User> mUsers;

    public LivePictureAdapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public LivePictureAdapter.LivePictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_picture, parent, false);
        return new LivePictureHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LivePictureAdapter.LivePictureHolder holder, int position) {
        User uploadCurrent = mUsers.get(position);
        holder.usernameLive.setText(uploadCurrent.getUsername());
        holder.followersLive.setText("0");

        if (uploadCurrent.getImageURL().isEmpty()) {
            holder.imageLive.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get().load(uploadCurrent.getImageURL())
                    .fit().centerCrop()
                    .into(holder.imageLive);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class LivePictureHolder extends RecyclerView.ViewHolder {

        public TextView usernameLive, followersLive;
        public ImageView imageLive;

        public LivePictureHolder(@NonNull final View itemView) {
            super(itemView);

            usernameLive = itemView.findViewById(R.id.live_username_textview);
            followersLive = itemView.findViewById(R.id.live_followers_textview);
            imageLive = itemView.findViewById(R.id.live_userpic_imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    User selectedUser = mUsers.get(position);
//                                int amountLikes = 0;
                    String selectedUserUsername = selectedUser.getUsername();
                    FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS).child(selectedUserUsername).child(KEY_STREAM_PLAYBACK_URL).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String playbackURL = snapshot.getValue(String.class);
                                Intent intent = new Intent(mContext , LivePlayerActivity.class);
//                                intent.putExtra(KEY_LIKES, amountLikes);
                                intent.putExtra(KEY_TYPE, STREAM_TYPE);
                                intent.putExtra(KEY_URI, playbackURL);

                                mContext.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
        }
    }
}

