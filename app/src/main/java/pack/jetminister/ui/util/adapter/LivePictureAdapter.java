package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
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
import pack.jetminister.ui.activities.PlaybackActivity;


import static java.text.Normalizer.Form.NFD;
import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.LiveStream.KEY_STREAM_USERNAME;
import static pack.jetminister.data.User.KEY_FOLLOWERS;
import static pack.jetminister.data.User.KEY_IMAGE_URL;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class LivePictureAdapter extends RecyclerView.Adapter<LivePictureAdapter.LivePictureHolder>
        implements Filterable
{

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private DatabaseReference streamersRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
    private Context mContext;
    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mStreamerUsernames;
    private List<String> mFilteredStreamerUsernames;


    public LivePictureAdapter(Context context,
                              List<String> allStreamerIDs,
                              List<String> filteredStreamerIDs,
                              List<String> streamerUsernames,
                              List<String> filteredStreamerUsernames) {
        mContext = context;
        mAllStreamerIDs = allStreamerIDs;
        mFilteredStreamerIDs = filteredStreamerIDs;
        mStreamerUsernames = streamerUsernames;
        mFilteredStreamerUsernames = filteredStreamerUsernames;
    }

    @NonNull
    @Override
    public LivePictureAdapter.LivePictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_picture, parent, false);
        return new LivePictureHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LivePictureAdapter.LivePictureHolder holder, int position) {
        usersRef.child(mFilteredStreamerIDs.get(position))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                holder.usernameLive.setText(currentUsername);
                long followers = snapshot.child(KEY_FOLLOWERS).getChildrenCount();
                holder.followersLive.setText(String.valueOf(followers));
                String currentImageURL = snapshot.child(KEY_IMAGE_URL).getValue(String.class);
                if (currentImageURL.isEmpty()) {
                    holder.imageLive.setImageResource(R.drawable.ic_launcher_background);
                } else {
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
        return mFilteredStreamerIDs.size();
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

    public class LivePictureHolder extends RecyclerView.ViewHolder {

        public TextView usernameLive, followersLive;
        public ImageView imageLive;

        public LivePictureHolder(@NonNull final View itemView) {
            super(itemView);

            usernameLive = itemView.findViewById(R.id.live_username_textview);
            followersLive = itemView.findViewById(R.id.live_viewers_tv);
            imageLive = itemView.findViewById(R.id.live_userpic_imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String currentStreamerID = mAllStreamerIDs.get(position);
                    streamersRef.child(currentStreamerID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String currentStreamUsername = snapshot.child(KEY_STREAM_USERNAME).getValue(String.class);
                                    String currentPlaybackURL = snapshot.child(KEY_STREAM_PLAYBACK_URL).getValue(String.class);
                                    Intent intent = new Intent(mContext, PlaybackActivity.class);
                                    intent.putExtra(KEY_USER_ID, currentStreamerID);
                                    intent.putExtra(KEY_STREAM_USERNAME, currentStreamUsername);
                                    intent.putExtra(KEY_STREAM_PLAYBACK_URL, currentPlaybackURL);
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

