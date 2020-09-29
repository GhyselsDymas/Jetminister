package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.streamaxia.player.StreamaxiaPlayer;
import com.streamaxia.player.listener.StreamaxiaPlayerState;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import pack.jetminister.R;
import pack.jetminister.data.Comment;

import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_LIKES;
import static pack.jetminister.data.LiveStream.KEY_STREAM_USERNAME;
import static pack.jetminister.data.LiveStream.KEY_STREAM_VIEWERS;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USER_ID;
import static pack.jetminister.ui.util.adapter.LivePictureAdapter.KEY_URI;

public class LivePlayerActivity extends AppCompatActivity implements StreamaxiaPlayerState {
    private static final String TAG = "LivePlayerActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference streamersRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
    private TextView usernamePlayerTV, likesPlayerTV, viewersPlayerTV, statePlayerTV;
    private ProgressBar playerProgressBar;
    private ImageView profilePlayerIV, likePlayerIV, sharePlayerIV, playPauseIV;
    private SurfaceView playerSurfaceView;
    private AspectRatioFrameLayout playerAspectRatioLayout;
    private EditText commentHereET;

    private StreamaxiaPlayer streamPlayer = new StreamaxiaPlayer();

    private boolean isLiked;
    private String streamerUsername;
    private Uri broadcastURI;
    private int amountLikes;
    private String streamerID;

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
            if (playPauseIV.getVisibility() == View.GONE) {
                playPauseIV.setVisibility(View.VISIBLE);
                streamPlayer.pause();
                playerProgressBar.setVisibility(View.VISIBLE);
            } else {
                playPauseIV.setVisibility(View.GONE);
                playerProgressBar.setVisibility(View.GONE);
                int STREAM_TYPE = StreamaxiaPlayer.TYPE_HLS;
                streamPlayer.play(broadcastURI, STREAM_TYPE);
            }
        }
    };

    private TextView.OnEditorActionListener postCommentListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // Identifier of the action. This will be either the identifier you supplied,
            // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                addCommentToStream();
                return true;
            }

            return false;
        }
    };

    private KeyboardVisibilityEventListener keyboardVisibilityListener = new KeyboardVisibilityEventListener() {
        @Override
        public void onVisibilityChanged(boolean isOpen) {
            if (isOpen){
                likePlayerIV.setVisibility(View.INVISIBLE);
                likesPlayerTV.setVisibility(View.INVISIBLE);
                sharePlayerIV.setVisibility(View.INVISIBLE);
                profilePlayerIV.setVisibility(View.INVISIBLE);
            }else{
                likePlayerIV.setVisibility(View.VISIBLE);
                likesPlayerTV.setVisibility(View.VISIBLE);
                sharePlayerIV.setVisibility(View.VISIBLE);
                profilePlayerIV.setVisibility(View.VISIBLE);
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
        viewersPlayerTV = findViewById(R.id.player_tv_watching);
        likesPlayerTV = findViewById(R.id.player_tv_amount_likes);
        statePlayerTV = findViewById(R.id.tv_live_player_state);
        profilePlayerIV = findViewById(R.id.player_iv_profile);
        likePlayerIV = findViewById(R.id.player_iv_like);
        sharePlayerIV = findViewById(R.id.player_iv_share);
        playPauseIV = findViewById(R.id.player_iv_play);
        playerSurfaceView = findViewById(R.id.player_surface_view);
        playerAspectRatioLayout = findViewById(R.id.player_aspect_ratio);
        playerProgressBar = findViewById(R.id.player_progress_bar);
        commentHereET = findViewById(R.id.ET_comment_here);

        getStreamerInfo();
        showAmountLikes();
        addCurrentViewer();
        showAmountViewers();

        isLiked = false;
        likePlayerIV.setImageResource(R.drawable.ic_like_border_white_24);
        likesPlayerTV.setVisibility(View.VISIBLE);
        likesPlayerTV.setText(String.valueOf(amountLikes));
        usernamePlayerTV.setText(streamerUsername);
        likePlayerIV.setOnClickListener(likeListener);
        playerAspectRatioLayout.setOnClickListener(playPauseListener);
        commentHereET.setOnEditorActionListener(postCommentListener);

        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener);

        initRTMPExoPlayer();
    }

    private void addCommentToStream() {
        long commentID = System.currentTimeMillis();
        streamersRef.child(currentUser.getUid()).child(KEY_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                Comment comment = new Comment(currentUser.getUid(), username, commentHereET.getText().toString());
                streamersRef.child("comments").child(String.valueOf(commentID)).setValue(comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addCurrentViewer(){
        streamersRef.child(streamerID).child(KEY_STREAM_VIEWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountViewers = snapshot.getValue(Integer.class);
                        amountViewers++;
                        viewersPlayerTV.setText(String.valueOf(amountViewers));
                        streamersRef.child(streamerID).child(KEY_STREAM_VIEWERS).setValue(amountViewers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showAmountViewers() {
        streamersRef.child(streamerID).child(KEY_STREAM_VIEWERS)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int amountViewers = snapshot.getValue(Integer.class);
                viewersPlayerTV.setText(String.valueOf(amountViewers));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showAmountLikes() {
        streamersRef.child(streamerID).child(KEY_STREAM_LIKES)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                amountLikes = snapshot.getValue(Integer.class);
                likesPlayerTV.setText(String.valueOf(amountLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void getStreamerInfo() {
        Bundle extras = getIntent().getExtras();
        streamerUsername = extras.getString(KEY_STREAM_USERNAME);
        broadcastURI = Uri.parse(extras.getString(KEY_URI));
        streamerID = extras.getString(KEY_USER_ID);
    }

    private void initRTMPExoPlayer() {
        streamPlayer.initStreamaxiaPlayer(
                playerSurfaceView,
                playerAspectRatioLayout,
                statePlayerTV,
                this,
                this,
                broadcastURI);
    }

    private void likeUnlike() {
        if (!isLiked) {
            amountLikes++;
            streamersRef.child(streamerID).child(KEY_STREAM_LIKES).setValue(amountLikes);
            likePlayerIV.setImageResource(R.drawable.ic_like_fill_white_24);
            isLiked = true;
        } else {
            amountLikes--;
            streamersRef.child(streamerID).child(KEY_STREAM_LIKES).setValue(amountLikes);
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