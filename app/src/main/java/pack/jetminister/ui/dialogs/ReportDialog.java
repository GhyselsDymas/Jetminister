package pack.jetminister.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Reports;
import pack.jetminister.data.User;

import static pack.jetminister.data.Reports.KEY_REASON;
import static pack.jetminister.data.Reports.KEY_REPORTS;
import static pack.jetminister.data.Reports.KEY_REPORT_BODY;

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

        String streamerID= getArguments().getString("msg");

        builder.setTitle(R.string.report_dialog_title);
        builder.setMessage(R.string.report_dialog_message);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

// first one
        Spinner spinner = new Spinner(mContext);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.reportReasons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPadding(0, 0 ,0 ,80);

        layout.addView(spinner);

// second one
        final EditText edittext = new EditText(mContext);
        edittext.setHint(R.string.report_dialog_reasons);
        layout.addView(edittext);

        layout.setPadding(40, 10, 40, 10);

        builder.setView(layout);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO: update database with new report info

                final DatabaseReference currentPath = usersDatabaseRef.child(streamerID).child(KEY_REPORTS).child(uID);
                currentPath.child(KEY_REASON).setValue(spinner.getSelectedItem());
                currentPath.child(KEY_REPORT_BODY).setValue(edittext.getText().toString());

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
