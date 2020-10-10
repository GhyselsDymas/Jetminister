package pack.jetminister.ui.util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Report;

import static pack.jetminister.data.Report.KEY_REPORT;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportAdminHolder> {

    private static final String TAG = "ReportAdminAdapter";

    private Context mContext;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private List<Report> mReports;

    public ReportAdapter(Context context, List<Report> reports) {
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
        String reportSubjectID = String.valueOf(currentReport.getSubjectID());
        String reportLoggerID = String.valueOf(currentReport.getLoggerID());
        String reportReason = String.valueOf(currentReport.getReason());
        usersRef.child(reportLoggerID).child(KEY_USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String reportLoggerUsername = snapshot.getValue(String.class);
                        holder.usernameReportLoggerTV.setText(String.format(reportLoggerUsername));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        usersRef.child(reportSubjectID).child(KEY_USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String reportSubjectUsername = snapshot.getValue(String.class);
                        holder.usernameReportSubjectTV.setText(String.format(reportSubjectUsername));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        holder.reasonReportAdmin.setText(reportReason);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    public class ReportAdminHolder extends RecyclerView.ViewHolder {

        public TextView usernameReportLoggerTV, usernameReportSubjectTV, reasonReportAdmin;

        public ReportAdminHolder(@NonNull View itemView) {
            super(itemView);
            usernameReportLoggerTV = itemView.findViewById(R.id.admin_detail_tv_report_logger);
                usernameReportSubjectTV = itemView.findViewById(R.id.admin_detail_tv_report_subject);
            reasonReportAdmin = itemView.findViewById(R.id.admin_detail_tv_report_reason);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Report report = mReports.get(position);
                    String reporterID = report.getLoggerID();
                    String reportBody = report.getReportBody();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(KEY_REPORT.toUpperCase())
                            .setMessage(String.format("%s \n\n%s", reporterID, reportBody))
                            .show();
                }
            });
        }
    }
}

