package pack.jetminister.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.streamaxia.android.CameraPreview;
import com.streamaxia.android.StreamaxiaPublisher;
import com.streamaxia.android.handlers.EncoderHandler;
import com.streamaxia.android.handlers.RecordHandler;
import com.streamaxia.android.handlers.RtmpHandler;

import java.io.IOException;
import java.net.SocketException;

import pack.jetminister.R;

public class LiveBroadcastActivity
            extends AppCompatActivity
            implements RtmpHandler.RtmpListener,
                    RecordHandler.RecordListener,
                    EncoderHandler.EncodeListener{

    public final static String STREAM_NAME = "JetStream";
    public final static int BITRATE = 500;
    public final static int WIDTH = 720;
    public final static int HEIGHT = 1280;

    private StreamaxiaPublisher broadcastPublisher;
    private CameraPreview previewCameraBroadcast;
    private TextView startStopBroadcastTV, stateBroadcastTV;
    private Chronometer broadcastChronometer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_broadcast);
        startStopBroadcastTV = findViewById(R.id.tv_live_broadcast_startstop);
        stateBroadcastTV = findViewById(R.id.tv_live_broadcast_state);
        broadcastChronometer = findViewById(R.id.chronometer_live_broadcast);
        previewCameraBroadcast = findViewById(R.id.cam_preview_live_broadcast);

        broadcastPublisher = new StreamaxiaPublisher(previewCameraBroadcast, this);

        broadcastPublisher.setEncoderHandler(new EncoderHandler(this));
        broadcastPublisher.setRtmpHandler(new RtmpHandler(this));
        broadcastPublisher.setRecordEventHandler(new RecordHandler(this));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
        previewCameraBroadcast.startCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            stopStreaming();
            stopChronometer();
            startStopBroadcastTV.setText("START");
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
    protected void onDestroy() {
        super.onDestroy();
        broadcastPublisher.stopPublish();
        broadcastPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        broadcastPublisher.setScreenOrientation(newConfig.orientation);
    }

    private void stopStreaming() {
        broadcastPublisher.stopPublish();
    }


    private void setStatusMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stateBroadcastTV.setText("[" + msg + "]");
            }
        });
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

    }

    @Override
    public void onRtmpConnecting(String s) {

    }

    @Override
    public void onRtmpConnected(String s) {

    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {

    }

    @Override
    public void onRtmpDisconnected() {

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

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            broadcastPublisher.stopPublish();
        } catch (Exception e1) {
            // Ignore
        }
    }

    private void stopChronometer() {
        broadcastChronometer.setBase(SystemClock.elapsedRealtime());
        broadcastChronometer.stop();
    }

}
