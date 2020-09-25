package pack.jetminister.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import pack.jetminister.data.SourceConnectionInformation;
import pack.jetminister.data.User;
import pack.jetminister.data.WowzaRestApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAM;
import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_ID;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.SourceConnectionInformation.KEY_STREAM_PUBLISH_URL;
import static pack.jetminister.data.User.KEY_LOCATION;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;

public class LiveBroadcastActivity
        extends AppCompatActivity
        implements RtmpHandler.RtmpListener,
        RecordHandler.RecordListener,
        EncoderHandler.EncodeListener {

    private static final String TAG = "LiveBroadcastActivity";
    private static final String API_KEY_DYMAS = "6gs5AUUZ20A7NSQPF3fFMZ3aD2bCOenpqfPQxl5qDIb6V36VW2FPUsnfdbUv3117";
    private static final String API_KEY_DAVID = "ffwQUAgvgFL310lMtP1O5Ee9rvnrg5bH8TgufZWHDAn2EHDeMJuWnKrdrVZU3356";
    private static final String ACCESS_KEY_DYMAS = "9nDkUi9yAe0j0BQIK7n9vaKlLIgMJQ49rHXOdrnMA2cA0iaZCWQQH8APaHJe305c";
    private static final String ACCESS_KEY_DAVID = "zaXG6RyhKF8DPQJTJNmG2n4Zcx98eU4mDmXmr2OKFgZQ7fz19AKtmYkqpYtd3334";

    public final static int BITRATE = 500;
    public final static int WIDTH = 720;
    public final static int HEIGHT = 1280;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String uID = mAuth.getCurrentUser().getUid();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private DatabaseReference liveStreamsRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
    private WowzaRestApi wowzaRestApi;

    private StreamaxiaPublisher broadcastPublisher;
    private Handler startHandler, checkHandler;
    private CameraPreview previewCameraBroadcast;
    private TextView startBroadcastTV, stopBroadcastTV, stateBroadcastTV;
    private Chronometer broadcastChronometer;
    private ProgressBar progressBar;
    ImageView liveIconIV, publishIcon;

    private View.OnClickListener publishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usersRef.child(uID).child(KEY_LIVE_STREAM).child(KEY_STREAM_PUBLISH_URL).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String publishURL = snapshot.getValue(String.class);
                        startBroadcast(publishURL);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    };

    private View.OnClickListener activateStreamListener = v -> {
        usersRef.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                if (snapshot.exists() && snapshot.hasChild(KEY_LIVE_STREAM)) {
                    String currentStreamID = snapshot.child(KEY_LIVE_STREAM).child(KEY_STREAM_ID).getValue(String.class);
                    String currentPublishURL = snapshot.child(KEY_LIVE_STREAM).child(KEY_STREAM_PUBLISH_URL).getValue(String.class);
                    if (currentStreamID.isEmpty() && currentPublishURL.isEmpty()) {
                        String userLocation = snapshot.child(KEY_LOCATION).getValue(String.class);
                        String currentUsername = snapshot.child(KEY_USERNAME).getValue(String.class);
                        LiveStream newLiveStream = new LiveStream(currentUsername, userLocation, "Hackermann", "1234azer");
                        Broadcast newBroadcast = new Broadcast(newLiveStream);
                        createLiveStream(newBroadcast);
                    } else {
                        activateStream(currentStreamID);
                        startRequestingStreamState();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    };

    private View.OnClickListener deactivateStreamListener = v -> {
        stopBroadcast();
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
        hideStatusBar();

        startBroadcastTV = findViewById(R.id.tv_live_broadcast_start);
        stopBroadcastTV = findViewById(R.id.tv_live_broadcast_stop);
        stateBroadcastTV = findViewById(R.id.tv_live_broadcast_state);
        progressBar = findViewById(R.id.broadcast_progressbar);
        broadcastChronometer = findViewById(R.id.chronometer_live_broadcast);
        previewCameraBroadcast = findViewById(R.id.cam_preview_live_broadcast);
        liveIconIV = findViewById(R.id.broadcast_iv_live);
        publishIcon = findViewById(R.id.live_broadcast_publish);
        publishIcon.setOnClickListener(publishListener);
        startBroadcastTV.setOnClickListener(activateStreamListener);
        stopBroadcastTV.setOnClickListener(deactivateStreamListener);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            stopPublishing();
//            deactivateStream();
            stopChronometer();
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
        deactivateStream();
    }

    @Override
    protected void onStop() {
        super.onStop();
        previewCameraBroadcast.stopCamera();
        broadcastPublisher.stopPublish();
        deactivateStream();
        broadcastPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastPublisher.stopPublish();
        deactivateStream();
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

    private void createLiveStream(Broadcast newBroadcast) {
        Call<Broadcast> call = wowzaRestApi.createLiveStream(API_KEY_DYMAS, ACCESS_KEY_DYMAS, newBroadcast);
        call.enqueue(new Callback<Broadcast>() {
            @Override
            public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, "createLiveStreamResponse " + response.code() + ": " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Broadcast broadcastResponse = response.body();
                if (broadcastResponse != null) {
                    LiveStream liveStream = broadcastResponse.getLiveStream();
                    String streamId = liveStream.getStreamId();
                    addLiveStreamToDatabase(broadcastResponse);
                    activateStream(streamId);
                    startRequestingStreamState();
                }
            }

            @Override
            public void onFailure(Call<Broadcast> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void addLiveStreamToDatabase(Broadcast broadcastResponse) {
        LiveStream currentLiveStream = broadcastResponse.getLiveStream();
        SourceConnectionInformation currentSourceInfo = currentLiveStream.getSourceConnectionInformation();
        usersRef.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        usersRef.child(uID).child(KEY_LIVE_STREAM).child(KEY_STREAM_ID).setValue(currentLiveStream.getStreamId());
        usersRef.child(uID).child(KEY_LIVE_STREAM).child(KEY_STREAM_PLAYBACK_URL).setValue(currentLiveStream.getPlaybackURL());
        usersRef.child(uID).child(KEY_LIVE_STREAM).child(KEY_STREAM_PUBLISH_URL).setValue(currentSourceInfo.toString());
        Log.d(TAG, "addLiveStreamToDataBaseResponse:\nstreamId = " + currentLiveStream.getStreamId() + "\nplaybackUrl = " + currentLiveStream.getPlaybackURL() + "\npublishUrl = " + currentSourceInfo.toString());
    }


    private void startRequestingStreamState() {
        startHandler = new Handler();
        startHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getStreamState();
                startHandler.postDelayed(this, 1000);
            }
        }, 50);
    }

    private void getStreamState() {
        usersRef.child(uID).child(KEY_LIVE_STREAM).addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String currentStreamID = snapshot.child(KEY_STREAM_ID).getValue(String.class);
                    String currentPublishURL = snapshot.child(KEY_STREAM_PUBLISH_URL).getValue(String.class);
                    Call<Broadcast> call = wowzaRestApi.getLiveStreamState(API_KEY_DYMAS, ACCESS_KEY_DYMAS, currentStreamID);
                    call.enqueue(new Callback<Broadcast>() {
                        @Override
                        public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.d(TAG, "getLiveStreamStateResponse " + response.code() + ": " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Broadcast broadcastResponse = response.body();
                            if (broadcastResponse != null) {
                                LiveStream checkedLivestream = broadcastResponse.getLiveStream();
                                String streamState = checkedLivestream.getStreamState();
                                Log.d(TAG, "get State " + streamState);
                                if (streamState.equals("started")) {
                                    startHandler.removeCallbacksAndMessages(null);
                                    publishIcon.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Broadcast> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void activateStream(String streamID) {
        Call<Broadcast> call = wowzaRestApi.startLiveStream(API_KEY_DYMAS, ACCESS_KEY_DYMAS, streamID);
        call.enqueue(new Callback<Broadcast>() {
            @Override
            public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, "activateStreamResponse " + response.code() + ": " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Broadcast> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void deactivateStream() {
        usersRef.child(uID).child(KEY_LIVE_STREAM).child(KEY_STREAM_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String currentStreamID = snapshot.getValue(String.class);
                    Call<Broadcast> call = wowzaRestApi.stopLiveStream(API_KEY_DYMAS, ACCESS_KEY_DYMAS, currentStreamID);
                    call.enqueue(new Callback<Broadcast>() {
                        @Override
                        public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(LiveBroadcastActivity.this, "Code :" + response.code(), Toast.LENGTH_SHORT).show();
                                try {
                                    Log.d(TAG, "deactivateStreamResponse " + response.code() + ": " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Broadcast> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void startBroadcast(String publishURL) {
                liveIconIV.setVisibility(View.VISIBLE);
                publishIcon.setVisibility(View.GONE);
                broadcastChronometer.setBase(SystemClock.elapsedRealtime());
                broadcastChronometer.start();
                broadcastPublisher.startPublish(publishURL);
    }

    private void stopBroadcast() {
        liveIconIV.setVisibility(View.GONE);
        stopChronometer();
        stopPublishing();
        deactivateStream();
    }

    private void stopPublishing() {
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
        stopBroadcastTV.setClickable(true);
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
