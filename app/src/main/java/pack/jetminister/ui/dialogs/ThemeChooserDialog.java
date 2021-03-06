package pack.jetminister.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.strictmode.CleartextNetworkViolation;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.ui.activities.LivestreamBroadcastActivity;

public class ThemeChooserDialog extends androidx.fragment.app.DialogFragment {

    private AppCompatActivity mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity)context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your theme to start the stream with.")
                .setItems(R.array.themes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        String[] myResArray = getResources().getStringArray(R.array.themes);
                        List<String> myResArrayList = Arrays.asList(myResArray);
                        Intent intent = new Intent(getActivity(), LivestreamBroadcastActivity.class);
                        startActivity(intent);
                        Toast.makeText(mContext, myResArrayList.get(which) , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

        return builder.create();
    }

}