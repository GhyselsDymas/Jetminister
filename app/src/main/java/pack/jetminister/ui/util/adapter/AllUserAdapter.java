package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {

    public class AllUserViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameTop100, followerAmountTop100;
        public ImageView imageTop100;

        public AllUserViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTop100 = itemView.findViewById(R.id.username_top100);
            followerAmountTop100 = itemView.findViewById(R.id.followerAmount_top100);
            imageTop100 = itemView.findViewById(R.id.imageView_top100);
        }
    }


    private Context mContext;
    private List<User> mUsers;

    public AllUserAdapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public AllUserAdapter.AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_top100_page, parent, false);
        return new AllUserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserAdapter.AllUserViewHolder holder, int position) {
        User uploadCurrent = mUsers.get(position);
        holder.usernameTop100.setText(uploadCurrent.getUsername());
        holder.followerAmountTop100.setText("0");
//        Picasso.get().load(uploadCurrent.getImageURL())
//                .onlyScaleDown()
//                .into(holder.imageTop100);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
