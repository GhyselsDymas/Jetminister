package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.PlaybackActivity;
import pack.jetminister.ui.activities.StreamerProfileActivity;

import static java.text.Normalizer.Form.NFD;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USER_ID;

public class SearchBarAdapter extends RecyclerView.Adapter<SearchBarAdapter.SearchBarHolder> implements Filterable {

    private Context mContext;

    private List<String> mAllStreamerIDs;
    private List<String> mFilteredStreamerIDs;
    private List<String> mStreamerUsernames;
    private List<String> mFilteredStreamerUsernames;

    public SearchBarAdapter(Context context,
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
    public SearchBarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_search_bar, parent, false);
        return new SearchBarHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull SearchBarHolder holder, int position) {
        String currentStreamerName = mFilteredStreamerUsernames.get(position);
        holder.usernameSearchBar.setText(currentStreamerName);
        holder.followersSearchBar.setText("0");
    }

    @Override
    public int getItemCount() {
        return  mFilteredStreamerUsernames.size();
    }

    public class SearchBarHolder extends RecyclerView.ViewHolder {

        public TextView usernameSearchBar, followersSearchBar;

        public SearchBarHolder(@NonNull View itemView) {
            super(itemView);

            usernameSearchBar = itemView.findViewById(R.id.username_SearchBar);
            followersSearchBar = itemView.findViewById(R.id.followerAmount_SearchBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String currentStreamerID = mFilteredStreamerIDs.get(position);
                    String currentUsername = mFilteredStreamerUsernames.get(position);
                    Intent intent = new Intent(mContext , StreamerProfileActivity.class);
                    intent.putExtra(KEY_USER_ID, currentStreamerID);
                    intent.putExtra(KEY_USERNAME, currentUsername);
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
