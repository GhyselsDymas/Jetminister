package pack.jetminister.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import pack.jetminister.R;
import pack.jetminister.ui.activities.MainActivity;
import pack.jetminister.ui.activities.StreamerProfileActivity;

public class StreamEndedDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_stream_ended, null,false);

        builder.setView(dialogView).setTitle(R.string.player_stream_ended);

        TextView toLiveTV = dialogView.findViewById(R.id.stream_ended_live_tv);
        TextView toStreamerProfileTV = dialogView.findViewById(R.id.stream_ended_profile_tv);

        toLiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        toStreamerProfileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StreamerProfileActivity.class);
                startActivity(intent);
            }
        });

        return builder.create();
    }
}

