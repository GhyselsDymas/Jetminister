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

import com.squareup.picasso.Picasso;
import com.streamaxia.player.StreamaxiaPlayer;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.LivePlayerActivity;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder> {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_URI = "uri";
    public static final String KEY_TYPE = "type";
    private static final String STREAM_URI_RTMP = "rtmp://192.168.56.1:5000/";
    public static final int STREAM_TYPE_RTMP = StreamaxiaPlayer.TYPE_RTMP;


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
                    User currentUser = mUsers.get(position);
                    int amountLikes = 0;
                    String currentUsername = currentUser.getUsername();

                    Intent intent = new Intent(mContext , LivePlayerActivity.class);
                    intent.putExtra(KEY_LIKES, amountLikes);
                    intent.putExtra("username", currentUsername);
                    intent.putExtra(KEY_TYPE, STREAM_TYPE_RTMP);
                    intent.putExtra(KEY_URI, STREAM_URI_RTMP + "JetMinister/" + currentUsername);

                    mContext.startActivity(intent);
                }
            });
        }
    }
}

