package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.PlaybackActivity;
import pack.jetminister.ui.activities.StreamerProfileActivity;
import pack.jetminister.ui.dialogs.StreamerProfileErrorDialog;

import static java.text.Normalizer.Form.NFD;
import static pack.jetminister.data.User.KEY_FOLLOWERS;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class Top100Adapter extends RecyclerView.Adapter<Top100Adapter.Top100Holder> implements Filterable {

    private static final String TAG = "Top100Adapter";

    private Context mContext;

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mStreamerUsernames;
    private List<String> mFilteredStreamerUsernames;

    public Top100Adapter(Context context,
                         List<String> allStreamerIDs,
                         List<String> filteredStreamerIDs,
                         List<String> allStreamerUsernames,
                         List<String> filteredStreamerUsernames){
        mContext = context;
        mAllStreamerIDs = allStreamerIDs;
        mFilteredStreamerIDs = filteredStreamerIDs;
        mStreamerUsernames = allStreamerUsernames;
        mFilteredStreamerUsernames = filteredStreamerUsernames;
    }

    @NonNull
    @Override
    public Top100Adapter.Top100Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_top100_page, parent, false);
        return new Top100Holder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull Top100Adapter.Top100Holder holder, int position) {
        usersRef.child(mFilteredStreamerIDs.get(position))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                holder.usernameTop100.setText(currentUsername);
                long followers = snapshot.child(KEY_FOLLOWERS).getChildrenCount();
                holder.followersTop100.setText(String.valueOf(followers));
                String currentImageURL = snapshot.child(KEY_IMAGE_URL).getValue(String.class);
                if (currentImageURL.isEmpty()) {
                    holder.imageTop100.setImageResource(R.drawable.ic_launcher_background);
                } else {
                    Picasso.get().load(currentImageURL)
                            .fit().centerCrop()
                            .into(holder.imageTop100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredStreamerIDs.size();
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
                    String currentStreamerID = mFilteredStreamerIDs.get(position);
                    String currentStreamerUsername = mFilteredStreamerUsernames.get(position);
                    Intent intent = new Intent(mContext , StreamerProfileActivity.class);
                    intent.putExtra(KEY_USER_ID, currentStreamerID);
                    intent.putExtra(KEY_USERNAME, currentStreamerUsername);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                /*user input and matching data must be case insensitive and use Normalizer to ignore accented characters*/
                String input = Normalizer.normalize(constraint, NFD).toLowerCase();
                if (input.isEmpty()) {
                    mFilteredStreamerIDs = mAllStreamerIDs;
                    mFilteredStreamerUsernames = mStreamerUsernames;
                } else {
                    mFilteredStreamerIDs = mAllStreamerIDs;
                    mFilteredStreamerUsernames = mStreamerUsernames;
                    ArrayList<String> tempUsernames = new ArrayList<>();
                    ArrayList<String> tempIds = new ArrayList<>();
                    for (String element : mFilteredStreamerUsernames) {
                        if (Normalizer.normalize(element.toLowerCase(), NFD).contains(input)) {
                            int index = mFilteredStreamerUsernames.indexOf(element);
                            tempUsernames.add(element);
                            tempIds.add(mFilteredStreamerIDs.get(index));
                        }
                    }
                    mFilteredStreamerUsernames = tempUsernames;
                    mFilteredStreamerIDs = tempIds;
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
