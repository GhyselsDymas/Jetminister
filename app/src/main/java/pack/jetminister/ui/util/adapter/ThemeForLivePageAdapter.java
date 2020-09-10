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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class ThemeForLivePageAdapter extends RecyclerView.Adapter<ThemeForLivePageAdapter.themeForLivePageViewHolder>  {

    private RecyclerView mRecyclerView;
    private LivePictureAdapter mAdapter;
    private DatabaseReference mDatabaseRef;

    public class themeForLivePageViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTheme, readMoreTheme;

        public themeForLivePageViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTheme = itemView.findViewById(R.id.textView_theme);
            readMoreTheme = itemView.findViewById(R.id.textView_read_more);
        }
    }


    private Context mContext;
    private List<String> mThemes;
    private List<User> mUsers;

    public ThemeForLivePageAdapter(Context context, List<String> themes){
        mContext = context;
        String[] myResArray = context.getResources().getStringArray(R.array.themes);
        themes = Arrays.asList(myResArray);
        mThemes = themes;
    }

    @NonNull
    @Override
    public ThemeForLivePageAdapter.themeForLivePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_page, parent, false);

        return new ThemeForLivePageAdapter.themeForLivePageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeForLivePageAdapter.themeForLivePageViewHolder holder, int position) {
        String uploadCurrent = mThemes.get(position);
        holder.titleTheme.setText(uploadCurrent);
        holder.readMoreTheme.setText("See more " + uploadCurrent);

        mRecyclerView = holder.itemView.findViewById(R.id.recycler_theme_live_page);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mUsers = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    mUsers.add(user);
                }

                mAdapter = new LivePictureAdapter(mContext, mUsers);

                mRecyclerView.setAdapter(mAdapter);
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
}
