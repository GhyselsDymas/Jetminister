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

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.VideoTestActivity;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder> {

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

                    String currentUsername = currentUser.getUsername();

                    Intent intent = new Intent(mContext , VideoTestActivity.class);
                    intent.putExtra("username", currentUsername);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

