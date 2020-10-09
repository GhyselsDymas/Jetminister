package pack.jetminister.ui.util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Report;

import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportAdminHolder> {

    private static final String TAG = "ReportAdminAdapter";

    private Context mContext;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private List<Report> mReports;

    public ReportAdapter(Context context, List<Report> reports){
        mContext = context;
        mReports = reports;
    }

    @NonNull
    @Override
    public ReportAdapter.ReportAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_admin_reports, parent, false);
        return new ReportAdminHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportAdminHolder holder, int position) {
        Report currentReport = mReports.get(position);
        String reporterID = currentReport.getReporterId();
        String reportReason = currentReport.getReason();
        usersRef.child(reporterID).child(KEY_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reporterUsername = snapshot.getValue(String.class);
                holder.usernameReporterAdmin.setText(String.format("%s ( %s )",reporterUsername, reporterID ));
                holder.reasonReportAdmin.setText(reportReason);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    public class ReportAdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameReporterAdmin, reasonReportAdmin, bodyReportAdmin;

        public ReportAdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameReporterAdmin = itemView.findViewById(R.id.admin_reporter_username);
            reasonReportAdmin = itemView.findViewById(R.id.admin_report_reason);
            bodyReportAdmin = itemView.findViewById(R.id.admin_report_body);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Report report = mReports.get(position);
                    String reportBody = report.getReportBody();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.report_body)
                            .setMessage(reportBody)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                }
            });
        }
    }
}

