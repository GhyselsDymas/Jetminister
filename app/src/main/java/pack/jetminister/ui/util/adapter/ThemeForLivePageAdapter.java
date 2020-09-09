package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class ThemeForLivePageAdapter extends RecyclerView.Adapter<ThemeForLivePageAdapter.themeForLivePageViewHolder>  {


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
    }

    @Override
    public int getItemCount() {
        return mThemes.size();
    }
}
