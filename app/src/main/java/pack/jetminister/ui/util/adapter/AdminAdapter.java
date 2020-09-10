package pack.jetminister.ui.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import pack.jetminister.data.User;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminHolder> {

    public class AdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameAdmin;

        public AdminHolder(@NonNull View itemView) {
            super(itemView);

            usernameAdmin = itemView.findViewById(R.id.Textview_admin);
        }
    }


    private Context mContext;
    private List<User> mUsers;

    public AdminAdapter(Context context, List<User> users){
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin_page, parent, false);

        return new AdminAdapter.AdminHolder(v);
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
}
