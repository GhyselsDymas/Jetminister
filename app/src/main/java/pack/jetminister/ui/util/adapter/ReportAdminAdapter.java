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
import pack.jetminister.ui.activities.StreamerProfileActivity;

import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;


public class ReportAdminAdapter extends RecyclerView.Adapter<ReportAdminAdapter.ReportAdminHolder> {

    private static final String TAG = "ReportAdminAdapter";

    private Context mContext;

    public ReportAdminAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public ReportAdminAdapter.ReportAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin_reports, parent, false);
        return new ReportAdminHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdminAdapter.ReportAdminHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        //return mReports.size();
    }

    public class ReportAdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameReporterAdmin, reasonReportAdmin, bodyReportAdmin;

        public ReportAdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameReporterAdmin = itemView.findViewById(R.id.admin_reporter_username);
            reasonReportAdmin = itemView.findViewById(R.id.admin_report_reason);
            bodyReportAdmin = itemView.findViewById(R.id.admin_report_body);
        }
    }


}

