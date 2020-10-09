package pack.jetminister.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pack.jetminister.R;
import pack.jetminister.data.Report;

import static pack.jetminister.data.User.KEY_REPORTS_LOGGED;
import static pack.jetminister.data.User.KEY_REPORTS_RECEIVED;
import static pack.jetminister.data.User.KEY_STREAMER;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class ReportDialog extends androidx.fragment.app.DialogFragment {

    private AppCompatActivity mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String currentUserID = currentUser.getUid();
        Bundle data = getArguments();
        if (data != null) {
            String streamerID = data.getString(KEY_USER_ID);

            builder.setTitle(R.string.report_dialog_title);
            builder.setMessage(R.string.report_dialog_message);

            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            AppCompatSpinner reportSpinner = new AppCompatSpinner(mContext);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(), R.array.reportReasons, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            reportSpinner.setAdapter(adapter);
            reportSpinner.setPadding(0, 0, 0, 80);

            layout.addView(reportSpinner);

            final EditText reportReasonsET = new EditText(mContext);
            reportReasonsET.setHint(R.string.report_dialog_reasons);
            layout.addView(reportReasonsET);
            layout.setPadding(40, 10, 40, 10);

            builder.setView(layout);

            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String reportReason = String.valueOf(reportSpinner.getSelectedItem());
                    String reportBody = reportReasonsET.getText().toString();
                    Report newReport = new Report(currentUserID, reportReason, reportBody);
                    usersRef.child(streamerID).child(KEY_REPORTS_RECEIVED).push().setValue(newReport)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext, R.string.report_confirmation, Toast.LENGTH_SHORT).show();
                                }
                            });
                    usersRef.child(currentUserID).child(KEY_REPORTS_LOGGED).push().setValue(newReport);

                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        return builder.create();
    }
}
