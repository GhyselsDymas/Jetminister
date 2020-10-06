package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.LiveStream;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;

public class LiveThemeAdapter extends RecyclerView.Adapter<LiveThemeAdapter.LiveThemeHolder> {

    private static final String TAG = "LiveThemeAdapter";
    private LivePictureAdapter pictureAdapter;
    private Context mContext;
    private List<String> mThemes;


    public LiveThemeAdapter(Context context, List<String> themes) {
        mContext = context;
        mThemes = themes;
    }

    @NonNull
    @Override
    public LiveThemeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View themeCardView = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_theme, parent, false);

        return new LiveThemeHolder(themeCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LiveThemeHolder holder, int position) {
        final String currentTheme = mThemes.get(position);
        holder.streamersPerTheme = new ArrayList<>();
        holder.filteredStreamersPerTheme = new ArrayList<>();
        holder.streamerUsernames = new ArrayList<>();
        holder.filteredStreamerUsernames = new ArrayList<>();
        DatabaseReference streamsRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
        streamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.streamersPerTheme.clear();
                holder.filteredStreamersPerTheme.clear();
                holder.streamerUsernames.clear();
                holder.filteredStreamerUsernames.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String streamerID = postSnapshot.getKey();
                    LiveStream currentStream = postSnapshot.getValue(LiveStream.class);
                    String streamerTheme = currentStream.getTheme();
                    String streamerUsername = currentStream.getStreamUsername();
                    if (streamerTheme.equals(currentTheme)){
                        holder.streamersPerTheme.add(streamerID);
                        holder.filteredStreamersPerTheme.add(streamerID);
                        holder.streamerUsernames.add(streamerUsername);
                        holder.filteredStreamerUsernames.add(streamerUsername);
                    }
                }
                holder.titleLiveTheme.setText(currentTheme);
                holder.readMoreLiveTheme.setText(String.format("%s in %s", mContext.getResources().getString(R.string.see_more), currentTheme));
                holder.recyclerViewLive.setHasFixedSize(true);
                holder.recyclerViewLive.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                pictureAdapter = new LivePictureAdapter(mContext,
                        holder.streamersPerTheme,
                        holder.filteredStreamersPerTheme,
                        holder.streamerUsernames,
                        holder.filteredStreamerUsernames
                );
                holder.recyclerViewLive.setAdapter(pictureAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public LivePictureAdapter getPictureAdapter() {
        return pictureAdapter;
    }

    @Override
    public int getItemCount() {
        return mThemes.size();
    }

    public static class LiveThemeHolder extends RecyclerView.ViewHolder {
        public List<String> streamersPerTheme;
        public List<String> filteredStreamersPerTheme;
        public List<String> streamerUsernames;
        public List<String> filteredStreamerUsernames;
        public TextView titleLiveTheme, readMoreLiveTheme;
        public RecyclerView recyclerViewLive;

        public LiveThemeHolder(@NonNull View itemView) {
            super(itemView);
            titleLiveTheme = itemView.findViewById(R.id.tv_theme);
            readMoreLiveTheme = itemView.findViewById(R.id.tv_see_more);
            recyclerViewLive = itemView.findViewById(R.id.rv_live_picture);
        }

    }






}

