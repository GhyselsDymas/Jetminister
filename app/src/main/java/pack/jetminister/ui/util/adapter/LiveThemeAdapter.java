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
import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.User;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAM;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;

public class LiveThemeAdapter extends RecyclerView.Adapter<LiveThemeAdapter.LiveThemeHolder>  {

    private LivePictureAdapter mAdapter;
    private Context mContext;
    private List<String> mThemes;
    private List<User> mUsers;
    private List<String> mLiveStreams;

    public LiveThemeAdapter(Context context){
        mContext = context;
        String[] myResArray = context.getResources().getStringArray(R.array.themes);
        mThemes = Arrays.asList(myResArray);
    }

    @NonNull
    @Override
    public LiveThemeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View themeCardView = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_theme, parent, false);
        return new LiveThemeHolder(themeCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LiveThemeHolder holder, int position) {
        final String uploadCurrent = mThemes.get(position);
        mUsers = new ArrayList<>();
        mLiveStreams = new ArrayList<>();
        DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        usersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    String playbackURL = postSnapshot.child(KEY_LIVE_STREAM).child(KEY_STREAM_PLAYBACK_URL).getValue(String.class);
                    mUsers.add(user);
                    mLiveStreams.add(playbackURL);

                }

                holder.titleLiveTheme.setText(uploadCurrent);
                holder.readMoreLiveTheme.setText(String.format("%s %s", mContext.getResources().getString(R.string.see_more_tv), uploadCurrent));
                holder.recyclerViewLive.setHasFixedSize(true);
                holder.recyclerViewLive.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                mAdapter = new LivePictureAdapter(mContext, mUsers);
                holder.recyclerViewLive.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThemes.size();
    }

    public static class LiveThemeHolder extends RecyclerView.ViewHolder {

        public TextView titleLiveTheme, readMoreLiveTheme;
        public RecyclerView recyclerViewLive;

        public LiveThemeHolder(@NonNull View itemView) {
            super(itemView);
            titleLiveTheme = itemView.findViewById(R.id.textView_theme);
            readMoreLiveTheme = itemView.findViewById(R.id.textView_read_more);
            recyclerViewLive = itemView.findViewById(R.id.rv_live_picture);
        }
    }

}

