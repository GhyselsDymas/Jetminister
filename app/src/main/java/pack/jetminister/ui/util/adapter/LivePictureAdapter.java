package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
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
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.LivePlayerActivity;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAM;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder> {

    public static final String KEY_URI = "uri";
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    public static final int STREAM_TYPE = StreamaxiaPlayer.TYPE_HLS;
    private Context mContext;
//    private List<User> mUsers;
//    private List<String> mLivestreams;
    private List<String> mStreamerIDs;

    public LivePictureAdapter(Context context,
//                              List<User> users,
//                              List<String> liveStreams,
                              List<String> streamerIDs){
        mContext = context;
//        mUsers = users;
//        mLivestreams = liveStreams;
        mStreamerIDs = streamerIDs;
    }

    @NonNull
    @Override
    public LivePictureAdapter.LivePictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_picture, parent, false);
        return new LivePictureHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LivePictureAdapter.LivePictureHolder holder, int position) {
//        User uploadCurrent = mUsers.get(position);
//        String currentPlaybackURL = mLivestreams.get(position);
         usersRef.child(mStreamerIDs.get(position)).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                 String currentImageURL = snapshot.child(KEY_IMAGE_URL).getValue(String.class);
                 holder.usernameLive.setText(currentUsername);
                 holder.followersLive.setText("0");
                 if (currentImageURL.isEmpty()) {
                     holder.imageLive.setImageResource(R.drawable.ic_launcher_background);
                 } else{
                     Picasso.get().load(currentImageURL)
                             .fit().centerCrop()
                             .into(holder.imageLive);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


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
//                    User currentUser = mUsers.get(position);
//                    String currentPlaybackURL = mLivestreams.get(position);
                    String currentStreamerID = mStreamerIDs.get(position);
                    usersRef.child(currentStreamerID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                            String currentPlaybackURL = snapshot.child(KEY_LIVE_STREAM).child(KEY_STREAM_PLAYBACK_URL).getValue(String.class);
                            Intent intent = new Intent(mContext , LivePlayerActivity.class);
                            intent.putExtra("streamerID", currentStreamerID);
                            intent.putExtra(KEY_USERNAME, currentUsername);
                            intent.putExtra(KEY_URI, currentPlaybackURL);

                            mContext.startActivity(intent);
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

