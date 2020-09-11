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

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class Top100Adapter extends RecyclerView.Adapter<Top100Adapter.Top100Holder> {


    public class Top100Holder extends RecyclerView.ViewHolder {

        public TextView usernameTop100, followerAmountTop100;
        public ImageView imageTop100;

        public Top100Holder(@NonNull View itemView) {
            super(itemView);

            usernameTop100 = itemView.findViewById(R.id.username_top100);
            followerAmountTop100 = itemView.findViewById(R.id.followerAmount_top100);
            imageTop100 = itemView.findViewById(R.id.imageView_top100);
        }
    }


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
        holder.followerAmountTop100.setText("0");

        if (uploadCurrent.getImageURL().isEmpty()) {
            holder.imageTop100.setImageResource(R.drawable.ic_launcher_background);
        } else{
            Picasso.get().load(uploadCurrent.getImageURL())
                    .fit()
                    .into(holder.imageTop100);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
