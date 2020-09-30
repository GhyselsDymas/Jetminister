package pack.jetminister.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.activities.LiveBroadcastActivity;

public class DescriptionChangeDialog extends androidx.fragment.app.DialogFragment {

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

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText edittext = new EditText(mContext);
        edittext.setHint("your Description");
        layout.addView(edittext);

        layout.setPadding(40, 0 , 40 , 0);

        builder.setTitle("Add a description to your profile");
                builder.setMessage("Enter your profile description");
                builder.setView(layout);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String editTextValue = edittext.getText().toString();
                        currentUserDatabaseRef.child(User.KEY_DESCRIPTION).setValue(editTextValue);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
