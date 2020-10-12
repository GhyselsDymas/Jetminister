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
        String reportSubjectID = String.valueOf(currentReport.getAboutID());
        String reportLoggerID = String.valueOf(currentReport.getLoggerID());
        String reportReason = String.valueOf(currentReport.getReason());
        String reportTimestamp = String.valueOf(currentReport.getReportTimeStamp());
        usersRef.child(reportLoggerID).child(KEY_USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String reportLoggerUsername = snapshot.getValue(String.class);
                        holder.reportLoggerUsernameTV.setText(String.format(reportLoggerUsername));
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
                        holder.reportAboutUsernameTV.setText(String.format(reportSubjectUsername));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        holder.reportReasonTV.setText(reportReason);
        holder.reportTimestampTV.setText(reportTimestamp);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    public class ReportAdminHolder extends RecyclerView.ViewHolder {

        public TextView reportLoggerUsernameTV, reportAboutUsernameTV, reportReasonTV, reportTimestampTV;

        public ReportAdminHolder(@NonNull View itemView) {
            super(itemView);
            reportLoggerUsernameTV = itemView.findViewById(R.id.admin_detail_tv_report_logger);
            reportAboutUsernameTV = itemView.findViewById(R.id.admin_detail_tv_report_subject);
            reportReasonTV = itemView.findViewById(R.id.admin_detail_tv_report_reason);
            reportTimestampTV = itemView.findViewById(R.id.admin_detail_tv_report_timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Report report = mReports.get(position);
                    String loggerID = report.getLoggerID();
                    String reportBody = report.getReportBody();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(KEY_REPORT.toUpperCase())
                            .setMessage(reportBody)
                            .show();
                }
            });
        }
    }
}

