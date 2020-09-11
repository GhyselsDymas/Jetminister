package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminHolder> {


    private Context mContext;
    private List<User> mUsers;

    public AdminAdapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin, parent, false);

        return new AdminHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.AdminHolder holder, int position) {
        User uploadCurrent = mUsers.get(position);
        holder.usernameAdmin.setText(uploadCurrent.getUsername());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class AdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameAdmin;

        public AdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameAdmin = itemView.findViewById(R.id.Textview_admin);
        }
    }
}
