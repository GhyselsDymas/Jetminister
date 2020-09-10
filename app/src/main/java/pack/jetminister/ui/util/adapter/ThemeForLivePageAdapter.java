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
import pack.jetminister.data.User;

public class ThemeForLivePageAdapter extends RecyclerView.Adapter<ThemeForLivePageAdapter.ThemeForLivePageViewHolder>  {

    private RecyclerView mRecyclerView;
    private LivePictureAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private Context mContext;
    private List<String> mThemes;
    private List<User> mUsers = new ArrayList<>();

    public ThemeForLivePageAdapter(Context context){
        mContext = context;
        String[] myResArray = context.getResources().getStringArray(R.array.themes);
        mThemes = Arrays.asList(myResArray);

    }

    @NonNull
    @Override
    public ThemeForLivePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View themeCardView = LayoutInflater.from(mContext).inflate(R.layout.cardview_live_page, parent, false);
        return new ThemeForLivePageViewHolder(themeCardView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThemeForLivePageViewHolder holder, int position) {
        final String uploadCurrent = mThemes.get(position);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                holder.titleTheme.setText(uploadCurrent);
                holder.readMoreTheme.setText("See more " + uploadCurrent);
                holder.livePictureRecyclerView.setHasFixedSize(true);
                holder.livePictureRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                mAdapter = new LivePictureAdapter(mContext, mUsers);
                holder.livePictureRecyclerView.setAdapter(mAdapter);
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

    public class ThemeForLivePageViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTheme, readMoreTheme;
        public RecyclerView livePictureRecyclerView;

        public ThemeForLivePageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTheme = itemView.findViewById(R.id.textView_theme);
            readMoreTheme = itemView.findViewById(R.id.textView_read_more);
            livePictureRecyclerView = itemView.findViewById(R.id.recycler_theme_live_page);
        }
    }

}

