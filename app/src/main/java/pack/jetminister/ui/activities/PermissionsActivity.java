package pack.jetminister.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import pack.jetminister.R;

public class PermissionsActivity extends AppCompatActivity {

    private static final String TAG = "PermissionsActivity";

    MultiplePermissionsListener permissionsListener = new MultiplePermissionsListener() {
        @Override
        public void onPermissionsChecked(MultiplePermissionsReport report) {
            if (report.areAllPermissionsGranted()) {
                Intent intent = new Intent(PermissionsActivity.this, LivestreamBroadcastActivity.class);
                startActivity(intent);
                PermissionsActivity.this.finish();
            } else {
                Toast.makeText(PermissionsActivity.this, "Not all permissions checked", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
            token.continuePermissionRequest();
        }
    };

    MultiplePermissionsListener dialogPermissionsListener =
            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                    .withContext(this)
                    .withTitle("Camera and Audio")
                    .withMessage("You need both Camera and Audio permission to broadcast")
                    .build();

    MultiplePermissionsListener compositePermissionsListener = new CompositeMultiplePermissionsListener(permissionsListener, dialogPermissionsListener);

    PermissionRequestErrorListener errorListener = new PermissionRequestErrorListener() {
        @Override public void onError(DexterError error) {
            Log.e(TAG,  error.toString());
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA)
                .withListener(compositePermissionsListener)
                .withErrorListener(errorListener)
                //.onSameThread() not sure if this is entirely necessary
                .check();
    }
}