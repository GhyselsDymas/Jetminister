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
import pack.jetminister.ui.activities.PlaybackActivity;

public class Top100Adapter extends RecyclerView.Adapter<Top100Adapter.Top100Holder> {

    private Context mContext;
    private List<User> mUsers;

    public Top100Adapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public Top100Adapter.Top100Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_top100_page, parent, false);
        return new Top100Holder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull Top100Adapter.Top100Holder holder, int position) {
        User uploadCurrent = mUsers.get(position);
        holder.usernameTop100.setText(uploadCurrent.getUsername());
        holder.followersTop100.setText("0");

        if (uploadCurrent.getImageURL().isEmpty()) {
            holder.imageTop100.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get().load(uploadCurrent.getImageURL())
                    .fit().centerCrop()
                    .into(holder.imageTop100);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class Top100Holder extends RecyclerView.ViewHolder {

        public TextView usernameTop100, followersTop100;
        public ImageView imageTop100;

        public Top100Holder(@NonNull View itemView) {
            super(itemView);

            usernameTop100 = itemView.findViewById(R.id.username_top100);
            followersTop100 = itemView.findViewById(R.id.followerAmount_top100);
            imageTop100 = itemView.findViewById(R.id.imageView_top100);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    User currentUser = mUsers.get(position);

                    String currentUsername = currentUser.getUsername();

                    Intent intent = new Intent(mContext , PlaybackActivity.class);
                    intent.putExtra("username", currentUsername);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
