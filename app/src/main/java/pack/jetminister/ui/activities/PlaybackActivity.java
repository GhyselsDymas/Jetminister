package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theoplayer.android.api.THEOplayerView;
import com.theoplayer.android.api.event.EventListener;
import com.theoplayer.android.api.event.player.CanPlayEvent;
import com.theoplayer.android.api.event.player.EndedEvent;
import com.theoplayer.android.api.event.player.ErrorEvent;
import com.theoplayer.android.api.event.player.PauseEvent;
import com.theoplayer.android.api.event.player.PlayEvent;
import com.theoplayer.android.api.event.player.PlayerEventTypes;
import com.theoplayer.android.api.event.player.PlayingEvent;
import com.theoplayer.android.api.player.Player;
import com.theoplayer.android.api.source.SourceDescription;
import com.theoplayer.android.api.source.SourceType;
import com.theoplayer.android.api.source.TypedSource;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.Comment;
import pack.jetminister.ui.dialogs.StreamEndedDialog;
import pack.jetminister.ui.dialogs.StreamErrorDialog;
import pack.jetminister.ui.util.adapter.CommentAdapter;

import static pack.jetminister.data.Comment.KEY_COMMENTS;
import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_LIKES;
import static pack.jetminister.data.LiveStream.KEY_STREAM_PLAYBACK_URL;
import static pack.jetminister.data.LiveStream.KEY_STREAM_USERNAME;
import static pack.jetminister.data.LiveStream.KEY_STREAM_VIEWERS;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;

public class PlaybackActivity extends AppCompatActivity {

    private static final String TAG = "LivePlayerActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private DatabaseReference streamersRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);

    private TextView streamUsernameTV, streamLikesTV, streamViewersTV, playbackStateTV;
    private ProgressBar playbackProgressBar;
    private ImageView streamLiveIV, streamProfileIV, streamLikeIV, streamShareIV, playPauseIV, submitCommentIV;
    private THEOplayerView playerView;
    private EditText postCommentET;
    private RecyclerView recyclerViewComment;

    private boolean streamLiked;
    private String streamUsername;
    private String streamPlaybackURL;
    private int streamLikes;
    private String streamerUID;

    private CommentAdapter mAdapter;
    private List<Comment> mComments;

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            playPauseIV.setVisibility(View.GONE);
        }
    };

    private KeyboardVisibilityEventListener keyboardVisibilityListener = new KeyboardVisibilityEventListener() {
        @Override
        public void onVisibilityChanged(boolean isOpen) {
            updateIconsVisibility(isOpen);
        }
    };

    private View.OnClickListener likeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            likeUnlike();
        }
    };

    private View.OnClickListener playPauseBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(PlaybackActivity.this, "whatthefaack", Toast.LENGTH_SHORT).show();
            if (playerView.getPlayer().isPaused()) {
                playerView.getPlayer().play();
            } else {
                playerView.getPlayer().pause();
            }
        }
    };

    private View.OnClickListener streamProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            proceedToStreamerProfile();
        }
    };

    private View.OnClickListener submitCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!postCommentET.getText().toString().trim().isEmpty()) {
                String commentBody = postCommentET.getText().toString().trim();
                addCommentToStream(commentBody);
                postCommentET.getText().clear();
                hideKeyboard();
                postCommentET.clearFocus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_player);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }
        hideStatusBar();
        streamUsernameTV = findViewById(R.id.player_tv_username);
        streamViewersTV = findViewById(R.id.player_tv_watching);
        streamLikesTV = findViewById(R.id.player_tv_amount_likes);
        playbackStateTV = findViewById(R.id.player_tv_live_state);
        streamLiveIV = findViewById(R.id.player_iv_live);
        streamProfileIV = findViewById(R.id.player_iv_profile);
        streamLikeIV = findViewById(R.id.player_iv_like);
        streamShareIV = findViewById(R.id.player_iv_share);
        playPauseIV = findViewById(R.id.player_iv_play_pause);
        playerView = findViewById(R.id.player_theo_view);
        playbackProgressBar = findViewById(R.id.player_progress_bar);
        postCommentET = findViewById(R.id.player_et_comment);
        submitCommentIV = findViewById(R.id.player_iv_comment_submit);

        playerView.setOnClickListener(playPauseBtnListener);
        streamLiked = false;
        streamLikeIV.setImageResource(R.drawable.ic_like_border_white_24);
        streamLikesTV.setVisibility(View.VISIBLE);
        streamLikesTV.setText(String.valueOf(streamLikes));
        streamUsernameTV.setText(streamUsername);
        streamLikeIV.setOnClickListener(likeListener);
        submitCommentIV.setOnClickListener(submitCommentListener);
        streamProfileIV.setOnClickListener(streamProfileListener);
        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener);


        getStreamerInfo();
        showAmountLikes();
        addCurrentViewer();
        showAmountViewers();


        TypedSource typedSource = TypedSource.Builder
                .typedSource()
                .src(streamPlaybackURL)
                .type(SourceType.HLS)
                .build();
        SourceDescription sourceDescription = SourceDescription.Builder
                .sourceDescription(typedSource)
                .build();
        Player player = playerView.getPlayer();
        player.setSource(sourceDescription);
        player.addEventListener(PlayerEventTypes.PLAY, playEventListener);
        player.addEventListener(PlayerEventTypes.PAUSE, pauseEventListener);
        player.addEventListener(PlayerEventTypes.CANPLAY, readyToPlayListener);
        player.addEventListener(PlayerEventTypes.ERROR, streamErrorListener);
        player.addEventListener(PlayerEventTypes.ENDED, streamEndedListener);
        player.addEventListener(PlayerEventTypes.PLAYING, streamPlayingListener);


        recyclerViewComment = findViewById(R.id.player_recyclerview_comments);
        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerViewComment.setLayoutManager(linearLayoutManager);

        mComments = new ArrayList<>();
        DatabaseReference streamersDatabaseRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS).child(streamerUID).child(KEY_COMMENTS);

        streamersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mComments.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    mComments.add(comment);
                    Collections.reverse(mComments);
                    mAdapter = new CommentAdapter(PlaybackActivity.this, mComments);
                    recyclerViewComment.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        playerView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCurrentViewer();
        playerView.onDestroy();
    }

    private void getStreamerInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && !extras.isEmpty()) {
                streamUsername = extras.getString(KEY_STREAM_USERNAME);
//                streamPlaybackURL = extras.getString(KEY_STREAM_PLAYBACK_URL);
                streamerUID = extras.getString(KEY_USER_ID);
                streamPlaybackURL = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8";
            }
        } else {
            Toast.makeText(this, R.string.player_stream_error, Toast.LENGTH_SHORT).show();
            proceedToMain();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void updateIconsVisibility(boolean keyBoardIsOpen) {
        if (keyBoardIsOpen) {
            streamLikeIV.setVisibility(View.INVISIBLE);
            streamLikesTV.setVisibility(View.INVISIBLE);
            streamShareIV.setVisibility(View.INVISIBLE);
            streamProfileIV.setVisibility(View.INVISIBLE);
        } else {
            streamLikeIV.setVisibility(View.VISIBLE);
            streamLikesTV.setVisibility(View.VISIBLE);
            streamShareIV.setVisibility(View.VISIBLE);
            streamProfileIV.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void proceedToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void proceedToStreamerProfile() {
        Intent intent = new Intent(PlaybackActivity.this, StreamerProfileActivity.class);
        intent.putExtra(KEY_USER_ID, streamerUID);
        startActivity(intent);
    }

    private void addCurrentViewer() {
        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountViewers = snapshot.getValue(Integer.class);
                        amountViewers++;
                        streamViewersTV.setText(String.valueOf(amountViewers));
                        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS).setValue(amountViewers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void removeCurrentViewer() {
        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountViewers = snapshot.getValue(Integer.class);
                        amountViewers--;
                        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS).setValue(amountViewers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showAmountViewers() {
        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountViewers = snapshot.getValue(Integer.class);
                        streamViewersTV.setText(String.valueOf(amountViewers));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showAmountLikes() {
        streamersRef.child(streamerUID).child(KEY_STREAM_LIKES)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        streamLikes = snapshot.getValue(Integer.class);
                        streamLikesTV.setText(String.valueOf(streamLikes));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void likeUnlike() {
        if (!streamLiked) {
            streamLikes++;
            streamersRef.child(streamerUID).child(KEY_STREAM_LIKES).setValue(streamLikes);
            streamLikeIV.setImageResource(R.drawable.ic_like_fill_white_24);
            streamLiked = true;
        } else {
            streamLikes--;
            streamersRef.child(streamerUID).child(KEY_STREAM_LIKES).setValue(streamLikes);
            streamLikeIV.setImageResource(R.drawable.ic_like_border_white_24);
            streamLiked = false;
        }
    }

    private void addCommentToStream(String commentBody) {
        long commentID = System.currentTimeMillis();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid()).child(KEY_USERNAME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.getValue(String.class);
                            Comment newComment = new Comment(currentUser.getUid(), username, commentBody);
                            streamersRef.child(streamerUID).child(KEY_COMMENTS).child(String.valueOf(commentID)).setValue(newComment);
                            Log.d(TAG, "new comment: " + newComment.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            Toast.makeText(this, R.string.player_comment_error_not_registered, Toast.LENGTH_SHORT).show();
        }
    }
    private EventListener<CanPlayEvent> readyToPlayListener = new EventListener<CanPlayEvent>() {
        @Override
        public void handleEvent(CanPlayEvent canPlayEvent) {
            playbackProgressBar.setVisibility(View.GONE);
            playPauseIV.setImageResource(R.drawable.ic_play_white_24);
            playPauseIV.setVisibility(View.VISIBLE);
        }
    };


    private EventListener<PlayEvent> playEventListener = new EventListener<PlayEvent>() {
        @Override
        public void handleEvent(PlayEvent playEvent) {
            playPauseIV.setVisibility(View.GONE);
            playbackProgressBar.setVisibility(View.GONE);
            streamLiveIV.setVisibility(View.VISIBLE);
        }
    };

    private EventListener<PlayingEvent> streamPlayingListener = new EventListener<PlayingEvent>() {
        @Override
        public void handleEvent(PlayingEvent playingEvent) {
            playPauseIV.setVisibility(View.GONE);
            playbackProgressBar.setVisibility(View.GONE);
            streamLiveIV.setVisibility(View.VISIBLE);
        }
    };

    private EventListener<PauseEvent> pauseEventListener = new EventListener<PauseEvent>() {
        @Override
        public void handleEvent(PauseEvent pauseEvent) {
            playPauseIV.setVisibility(View.VISIBLE);
        }
    };

    private EventListener<ErrorEvent> streamErrorListener = new EventListener<ErrorEvent>() {
        @Override
        public void handleEvent(ErrorEvent errorEvent) {
            showStreamErrorDialog();
        }
    };

    private EventListener<EndedEvent> streamEndedListener = new EventListener<EndedEvent>() {
        @Override
        public void handleEvent(EndedEvent endedEvent) {
            playbackProgressBar.setVisibility(View.GONE);
            streamLiveIV.setVisibility(View.INVISIBLE);
            showStreamEndedDialog();
        }
    };

    private void showStreamEndedDialog() {
        StreamEndedDialog streamEndedDialog = StreamEndedDialog.newInstance(streamerUID);
        Bundle data = new Bundle();
        data.putString(KEY_USER_ID, streamerUID);
        streamEndedDialog.setArguments(data);
        streamEndedDialog.show(getSupportFragmentManager(), "stream_ended");
    }

    private void showStreamErrorDialog(){
        StreamErrorDialog streamErrorDialog = new StreamErrorDialog();
        streamErrorDialog.show(getSupportFragmentManager(), "stream_error");
    }
}


//    @Override
//    public void stateUNKNOWN() {
//        playbackProgressBar.setVisibility(View.VISIBLE);
//        streamLiveIV.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void stateIDLE() {
//        playbackProgressBar.setVisibility(View.VISIBLE);
//        streamLiveIV.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void stateBUFFERING() {
//        playbackProgressBar.setVisibility(View.VISIBLE);
//        streamLiveIV.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void statePREPARING() {
//        streamLiveIV.setVisibility(View.INVISIBLE);
//        playbackProgressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void stateREADY() {
//        playbackProgressBar.setVisibility(View.GONE);
//        playbackStateTV.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void stateENDED() {

//    }