package pack.jetminister.ui.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.Timer;

import de.tavendo.autobahn.WebSocket;
import io.antmedia.webrtcandroidframework.IWebRTCClient;
import io.antmedia.webrtcandroidframework.IWebRTCListener;
import io.antmedia.webrtcandroidframework.WebRTCClient;
import io.antmedia.webrtcandroidframework.apprtc.CallActivity;
import io.antmedia.webrtcandroidframework.apprtc.CallFragment;
import pack.jetminister.R;

import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_VIDEO_BITRATE;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_VIDEO_FPS;

public class BroadcastActivity extends AppCompatActivity implements IWebRTCListener {
    public static final String SERVER_ADDRESS = "127.0.0.1:5080";


    private String webRTCMode = IWebRTCClient.MODE_PUBLISH;

    public static final String SERVER_URL = "ws://" + SERVER_ADDRESS + "/WebRTCAppEE/websocket";
    public static final String REST_URL = "http://" + SERVER_ADDRESS + "/WebRTCAppEE/rest/v2";
    private CallFragment callFragment;

    private WebRTCClient webRTCClient;

    private Button startStreamingButton;
    private String operationName = "";
    private Timer timer;
    private String streamId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());

        setContentView(R.layout.activity_broadcast_);
        SurfaceViewRenderer cameraViewRenderer = findViewById(R.id.camera_view_renderer);
        SurfaceViewRenderer pipViewRenderer = findViewById(R.id.pip_view_renderer);

        startStreamingButton = (Button) findViewById(R.id.start_streaming_button);

        // Check for mandatory permissions.
        for (String permission : CallActivity.MANDATORY_PERMISSIONS) {
            if (this.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission " + permission + " is not granted", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (webRTCMode.equals(IWebRTCClient.MODE_PUBLISH)) {
            startStreamingButton.setText("Start Publishing");
            operationName = "Publishing";
        } else if (webRTCMode.equals(IWebRTCClient.MODE_PLAY)) {
            startStreamingButton.setText("Start Playing");
            operationName = "Playing";
        }

        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
        this.getIntent().putExtra(EXTRA_VIDEO_FPS, 30);
        this.getIntent().putExtra(EXTRA_VIDEO_BITRATE, 2500);
        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);

        webRTCClient = new WebRTCClient(this, this);

        //webRTCClient.setOpenFrontCamera(false);
        streamId = "stream1";
        String tokenId = "tokenId";

        webRTCClient.setVideoRenderers(pipViewRenderer, cameraViewRenderer);

        // this.getIntent().putExtra(CallActivity.EXTRA_VIDEO_FPS, 24);
        webRTCClient.init(SERVER_URL, streamId, webRTCMode, tokenId, this.getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        webRTCClient.stopStream();
    }

    public void startStreaming(View view) {

        if (!webRTCClient.isStreaming()) {
            ((Button) view).setText("Stop " + operationName);
            webRTCClient.startStream();
        } else {
            ((Button) view).setText("Start " + operationName);
            webRTCClient.stopStream();
        }


    }

    public void onOffVideo(View view) {
        if (webRTCClient.isVideoOn()) {
            webRTCClient.disableVideo();
        }
        else {
            webRTCClient.enableVideo();
        }
    }

    public void onOffAudio(View view) {
        if (webRTCClient.isAudioOn()) {
            webRTCClient.disableAudio();
        }
        else {
            webRTCClient.enableAudio();
        }
    }

    @Override
    public void onPlayStarted() {
        Log.w(getClass().getSimpleName(), "onPlayStarted");
        Toast.makeText(this, "Play started", Toast.LENGTH_LONG).show();
        webRTCClient.switchVideoScaling(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    }

    @Override
    public void onPublishStarted() {
        Log.w(getClass().getSimpleName(), "onPublishStarted");
        Toast.makeText(this, "Publish started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPublishFinished() {
        Log.w(getClass().getSimpleName(), "onPublishFinished");
        Toast.makeText(this, "Publish finished", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPlayFinished() {
        Log.w(getClass().getSimpleName(), "onPlayFinished");
        Toast.makeText(this, "Play finished", Toast.LENGTH_LONG).show();
    }

    @Override
    public void noStreamExistsToPlay() {
        Log.w(getClass().getSimpleName(), "noStreamExistsToPlay");
        Toast.makeText(this, "No stream exist to play", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onError(String description) {
        Toast.makeText(this, "Error: "  +description , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code) {
        Toast.makeText(this, "Signal channel closed with code " + code, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Log.w(getClass().getSimpleName(), "disconnected");
        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();

        startStreamingButton.setText("Start " + operationName);
        //finish();
    }

    @Override
    public void onBitrateMeasurement(String streamId, int targetBitrate, int videoBitrate, int audioBitrate) {
        Log.e(getClass().getSimpleName(), "st:"+streamId+" tb:"+targetBitrate+" vb:"+videoBitrate+" ab:"+audioBitrate);
        if(targetBitrate < (videoBitrate+audioBitrate)) {
            Toast.makeText(this, "low bandwidth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIceConnected() {

    }

    @Override
    public void onIceDisconnected() {

    }

    //wtf is this
    @Override
    public void onTrackList(String[] tracks) {

    }
}
