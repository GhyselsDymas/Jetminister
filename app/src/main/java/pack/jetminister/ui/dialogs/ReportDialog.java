package pack.jetminister.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pack.jetminister.R;

import static pack.jetminister.data.Report.KEY_REASON;
import static pack.jetminister.data.Report.KEY_REPORTS;
import static pack.jetminister.data.Report.KEY_REPORT_BODY;
import static pack.jetminister.data.User.KEY_STREAMER;

public class ReportDialog extends androidx.fragment.app.DialogFragment{

    private AppCompatActivity mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String uID = currentUser.getUid();
        final DatabaseReference currentUserDatabaseRef = usersDatabaseRef.child(uID);
//        String editTextValue = edittext.getText().toString();
//        currentUserDatabaseRef.child(User.KEY_DESCRIPTION).setValue(editTextValue);

        String streamerID= getArguments().getString(KEY_STREAMER);

        builder.setTitle(R.string.report_dialog_title);
        builder.setMessage(R.string.report_dialog_message);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

// first one
        AppCompatSpinner reportSpinner = new AppCompatSpinner(mContext);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.reportReasons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportSpinner.setAdapter(adapter);
        reportSpinner.setPadding(0, 0 ,0 ,80);

        layout.addView(reportSpinner);

// second one
        final EditText reportReasonsET = new EditText(mContext);
        reportReasonsET.setHint(R.string.report_dialog_reasons);
        layout.addView(reportReasonsET);
        layout.setPadding(40, 10, 40, 10);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final DatabaseReference currentPath = usersDatabaseRef.child(streamerID).child(KEY_REPORTS).child(uID);
                currentPath.child(KEY_REASON).setValue(reportSpinner.getSelectedItem());
                currentPath.child(KEY_REPORT_BODY).setValue(reportReasonsET.getText().toString());

                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
