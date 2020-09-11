package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder> {

    public class LivePictureHolder extends RecyclerView.ViewHolder {

        public TextView liveUsername, liveFollowers;
        public ImageView livePicture;

        public LivePictureHolder(@NonNull View itemView) {
            super(itemView);

            liveUsername = itemView.findViewById(R.id.live_username_textview);
            liveFollowers = itemView.findViewById(R.id.live_followers_textview);
            livePicture = itemView.findViewById(R.id.live_userpic_imageView);
        }
    }

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

        return new LivePictureAdapter.LivePictureHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LivePictureAdapter.LivePictureHolder holder, int position) {
        User uploadCurrent = mUsers.get(position);
        holder.liveUsername.setText(uploadCurrent.getUsername());
        holder.liveFollowers.setText("0");


        if (uploadCurrent.getImageURL().isEmpty()) {
            holder.livePicture.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get().load(uploadCurrent.getImageURL())
                    .fit()
                    .into(holder.livePicture);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

