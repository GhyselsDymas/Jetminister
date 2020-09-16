package pack.jetminister.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.streamaxia.player.StreamaxiaPlayer;
import com.streamaxia.player.listener.StreamaxiaPlayerState;

import pack.jetminister.R;
import pack.jetminister.ui.util.adapter.LivePictureAdapter;

public class LivePlayerActivity extends AppCompatActivity implements StreamaxiaPlayerState {
    private static final String TAG = "LivePlayerActivity";
    private TextView usernamePlayerTV, likesPlayerTV, statePlayerTV;
    private ProgressBar playerProgressBar;
    private ImageView profilePlayerIV, likePlayerIV, sharePlayerIV, playPauseIV;
    private SurfaceView playerSurfaceView;
    private AspectRatioFrameLayout playerAspectRatioLayout;

    private StreamaxiaPlayer streamPlayer = new StreamaxiaPlayer();

    private boolean isLiked;

    private String usernameBroadcast;
    private Uri broadcastURI;
    private int amountLikes;
    private int STREAM_TYPE = 0;

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            playPauseIV.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            likeUnlike();
        }
    };

    private View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playPauseIV.getVisibility() == View.GONE){
                playPauseIV.setVisibility(View.VISIBLE);
                streamPlayer.pause();
                playerProgressBar.setVisibility(View.VISIBLE);
            } else {
                playPauseIV.setVisibility(View.GONE);
                playerProgressBar.setVisibility(View.GONE );
                streamPlayer.play(broadcastURI, STREAM_TYPE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }
        hideStatusBar();
        usernamePlayerTV = findViewById(R.id.player_tv_username);
        likesPlayerTV = findViewById(R.id.player_tv_amount_likes);
        statePlayerTV = findViewById(R.id.tv_live_player_state);
        profilePlayerIV = findViewById(R.id.player_iv_profile);
        likePlayerIV = findViewById(R.id.player_iv_like);
        sharePlayerIV = findViewById(R.id.player_iv_share);
        playPauseIV = findViewById(R.id.player_iv_play);
        playerSurfaceView = findViewById(R.id.player_surface_view);
        playerAspectRatioLayout = findViewById(R.id.player_aspect_ratio);
        playerProgressBar = findViewById(R.id.player_progress_bar);

        getExtras();

        isLiked = false;
        likePlayerIV.setImageResource(R.drawable.ic_like_border_white_24);
        likesPlayerTV.setVisibility(View.INVISIBLE);
        likesPlayerTV.setText(String.valueOf(amountLikes));
        likePlayerIV.setOnClickListener(likeListener);
        usernamePlayerTV.setText(usernameBroadcast);
        playerAspectRatioLayout.setOnClickListener(playPauseListener);

        initRTMPExoPlayer();
    }

    @Override
    public void stateENDED() {
        playerProgressBar.setVisibility(View.GONE);
        playPauseIV.setImageResource(R.drawable.ic_play_white_24);
    }

    @Override
    public void stateBUFFERING() {
        playerProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stateIDLE() {
        playerProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void statePREPARING() {
        playerProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stateREADY() {
        playerProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void stateUNKNOWN() {
        playerProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamPlayer.stop();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        usernameBroadcast = extras.getString(LivePictureAdapter.KEY_USERNAME);
        amountLikes = extras.getInt(LivePictureAdapter.KEY_LIKES);
        broadcastURI = Uri.parse(extras.getString(LivePictureAdapter.KEY_URI));
        STREAM_TYPE = extras.getInt(LivePictureAdapter.KEY_TYPE);

        //TODO: add username and amount of likes
    }

    private void initRTMPExoPlayer() {
        streamPlayer.initStreamaxiaPlayer(playerSurfaceView,
                playerAspectRatioLayout,
                statePlayerTV,
                this,
                this,
                broadcastURI);
    }

    private void likeUnlike() {
        if (!isLiked) {
            amountLikes++;
            likesPlayerTV.setText(amountLikes);
            likePlayerIV.setImageResource(R.drawable.ic_like_fill_white_24);
            isLiked = true;
        } else {
            amountLikes--;
            likesPlayerTV.setText(amountLikes);
            likePlayerIV.setImageResource(R.drawable.ic_like_border_white_24);
            isLiked = false;
        }

    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


}