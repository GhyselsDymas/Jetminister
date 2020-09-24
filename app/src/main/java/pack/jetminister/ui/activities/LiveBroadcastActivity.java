package pack.jetminister.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.streamaxia.android.CameraPreview;
import com.streamaxia.android.StreamaxiaPublisher;
import com.streamaxia.android.handlers.EncoderHandler;
import com.streamaxia.android.handlers.RecordHandler;
import com.streamaxia.android.handlers.RtmpHandler;
import com.streamaxia.android.utils.Size;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Broadcast;
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.WowzaRestApi;
import pack.jetminister.data.util.BroadcastLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pack.jetminister.data.User.KEY_LOCATION;
import static pack.jetminister.data.User.KEY_USERNAME;

public class LiveBroadcastActivity
        extends AppCompatActivity
        implements RtmpHandler.RtmpListener,
        RecordHandler.RecordListener,
        EncoderHandler.EncodeListener {

    private static final String TAG = "LiveBroadcastActivity";
    private static final String API_KEY = "6gs5AUUZ20A7NSQPF3fFMZ3aD2bCOenpqfPQxl5qDIb6V36VW2FPUsnfdbUv3117";
    private static final String ACCESS_KEY = "9nDkUi9yAe0j0BQIK7n9vaKlLIgMJQ49rHXOdrnMA2cA0iaZCWQQH8APaHJe305c";
    public final static int BITRATE = 500;
    public final static int WIDTH = 720;
    public final static int HEIGHT = 1280;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private WowzaRestApi wowzaRestApi;

    private LiveStream liveStream;
    private StreamaxiaPublisher broadcastPublisher;
    private CameraPreview previewCameraBroadcast;
    private TextView startStopBroadcastTV, stateBroadcastTV;
    private Chronometer broadcastChronometer;
    ImageView liveIconIV;

    private View.OnClickListener startStopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: AlertDialog to confirm broadcast
            startStopStream();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_broadcast);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }
        startStopBroadcastTV = findViewById(R.id.tv_live_broadcast_startstop);
        stateBroadcastTV = findViewById(R.id.tv_live_broadcast_state);
        broadcastChronometer = findViewById(R.id.chronometer_live_broadcast);
        previewCameraBroadcast = findViewById(R.id.cam_preview_live_broadcast);
        liveIconIV = findViewById(R.id.broadcast_iv_live);
        startStopBroadcastTV.setOnClickListener(startStopListener);
        hideStatusBar();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloud.wowza.com/api/v1.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        wowzaRestApi = retrofit.create(WowzaRestApi.class);

        broadcastPublisher = new StreamaxiaPublisher(previewCameraBroadcast, this);
        broadcastPublisher.setEncoderHandler(new EncoderHandler(this));
        broadcastPublisher.setRtmpHandler(new RtmpHandler(this));
        broadcastPublisher.setRecordEventHandler(new RecordHandler(this));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            previewCameraBroadcast.startCamera();
            setStreamerDefaultValues();
        }

        if (currentUser != null) {
            usersRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.hasChild(KEY_USERNAME) && snapshot.hasChild(KEY_LOCATION)){
                        String userLocation = snapshot.child(KEY_USERNAME).getValue(String.class);
                        String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                        LiveStream newLiveStream = new LiveStream(currentUsername, userLocation, "Hackermann", "1234azer");
                        Broadcast newBroadcast = new Broadcast(newLiveStream);
                        createLiveStream(newBroadcast);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void createLiveStream(Broadcast newBroadcast) {;
        Call<Broadcast> call = wowzaRestApi.createLiveStream(API_KEY, ACCESS_KEY, newBroadcast);
        call.enqueue(new Callback<Broadcast>() {
            @Override
            public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code() , Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, "Response " + response.code() + ": " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Broadcast broadcastResponse = response.body();
                if (broadcastResponse != null) {
                    LiveStream currentLiveStream = broadcastResponse.getLiveStream();
                    LiveBroadcastActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            liveStream = currentLiveStream;
                        }
                    });
                    Log.d(TAG, "onResponse:\nstreamId = " + currentLiveStream.getStreamId() + "\nplaybackUrl = " + currentLiveStream.getPlaybackURL());
                }
            }
            @Override
            public void onFailure(Call<Broadcast> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            stopStreaming();
            stopChronometer();
            startStopBroadcastTV.setText(getResources().getString(R.string.start));
        } else {
            Intent intent = new Intent(this, PermissionsActivity.class);
            startActivity(intent);
            Toast.makeText(this, R.string.broadcast_permission_notification, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        previewCameraBroadcast.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        previewCameraBroadcast.stopCamera();
        broadcastPublisher.stopPublish();
        broadcastPublisher.pauseRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
        previewCameraBroadcast.stopCamera();
        broadcastPublisher.stopPublish();
        broadcastPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastPublisher.stopPublish();
        broadcastPublisher.stopRecord();
    }


    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            broadcastPublisher.stopPublish();
        } catch (Exception e1) {
            // Ignore
        }
    }

    private void setStatusMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateBroadcastTV.setText("[" + msg + "]");
            }
        });
    }

    private void startStopStream() {
        if (currentUser != null) {
            String uID = currentUser.getUid();
            usersRef.child(uID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("username").exists()) {
                        String broadcastUsername = snapshot.child("username").getValue(String.class);
                        if (startStopBroadcastTV.getText().toString().trim().equals(getResources().getString(R.string.start))) {
                            startStopBroadcastTV.setText(getResources().getString(R.string.stop));
                            liveIconIV.setVisibility(View.VISIBLE);
                            broadcastChronometer.setBase(SystemClock.elapsedRealtime());
                            broadcastChronometer.start();
//                            broadcastPublisher.startPublish(STREAM_URI_RTMP
//                                    + "JetMinister/" + broadcastUsername);
//                            takeSnapshot();
                        } else {
                            startStopBroadcastTV.setText(getResources().getString(R.string.start));
                            liveIconIV.setVisibility(View.GONE);
                            stopChronometer();
                            stopStreaming();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void stopStreaming() {
        broadcastPublisher.stopPublish();
    }

    private void stopChronometer() {
        broadcastChronometer.setBase(SystemClock.elapsedRealtime());
        broadcastChronometer.stop();
    }

    private void setStreamerDefaultValues() {
        // Set one of the available resolutions
        List<Size> sizes = broadcastPublisher.getSupportedPictureSizes(this.getResources().getConfiguration().orientation);
        Size resolution = sizes.get(0);
        broadcastPublisher.setVideoOutputResolution(resolution.width, resolution.height, this.getResources().getConfiguration().orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        broadcastPublisher.setScreenOrientation(newConfig.orientation);
    }

    @Override
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String s) {

    }

    @Override
    public void onRecordFinished(String s) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpConnecting(String s) {
        setStatusMessage(s);
    }

    @Override
    public void onRtmpConnected(String s) {
        setStatusMessage(s);
        startStopBroadcastTV.setText(getResources().getString(R.string.stop));
    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {
        setStatusMessage("STOPPED");
    }

    @Override
    public void onRtmpDisconnected() {
        setStatusMessage("Disconnected");

    }

    @Override
    public void onRtmpVideoFpsChanged(double v) {

    }

    @Override
    public void onRtmpVideoBitrateChanged(double v) {

    }

    @Override
    public void onRtmpAudioBitrateChanged(double v) {

    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    @Override
    public void onRtmpAuthenticationg(String s) {
    }
}
