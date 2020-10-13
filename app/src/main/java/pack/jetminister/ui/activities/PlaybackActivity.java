package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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

public class PlaybackActivity extends AppCompatActivity implements StreamaxiaPlayerState {

    private static final String TAG = "LivePlayerActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private DatabaseReference streamersRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);

    private TextView streamUsernameTV, streamLikesTV, streamViewersTV, playbackStateTV;
    private ProgressBar playbackProgressBar;
    private ImageView streamLiveIV, streamProfileIV, streamLikeIV, streamShareIV, playPauseIV;
    private AppCompatToggleButton showCommentsToggleBtn;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private SurfaceView surfaceView;
    //    private THEOplayerView playerView;
    private StreamaxiaPlayer streamPlayer;
    private TextInputLayout postCommentTIL;
    private RecyclerView recyclerViewComment;

    private boolean streamLiked;
    private String streamUsername;
    private Uri streamPlaybackURI;
    private int streamLikes;
    private String streamerUID;

    private CommentAdapter mAdapter;
    private List<Comment> mComments;

    private View.OnClickListener playPauseBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playPauseIV.getVisibility() == View.GONE) {
                playPauseIV.setVisibility(View.VISIBLE);
                streamPlayer.pause();
                playbackProgressBar.setVisibility(View.VISIBLE);
            } else {
                playPauseIV.setVisibility(View.GONE);
                playbackProgressBar.setVisibility(View.GONE);
                streamPlayer.play(streamPlaybackURI, StreamaxiaPlayer.TYPE_HLS);
            }
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

    private CompoundButton.OnCheckedChangeListener showCommentsListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                showComments();
            } else {
                hideComments();
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
            if (!postCommentTIL.getEditText().getText().toString().trim().isEmpty()) {
                String commentBody = postCommentTIL.getEditText().getText().toString().trim();
                addCommentToStream(commentBody);
                postCommentTIL.getEditText().getText().clear();
                hideKeyboard();
                postCommentTIL.clearFocus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_playback);
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
        playbackProgressBar = findViewById(R.id.player_progress_bar);
        postCommentTIL = findViewById(R.id.player_til_comment);
        showCommentsToggleBtn = findViewById(R.id.player_toggle_comments);
        surfaceView = findViewById(R.id.stream_playback_player_view);
        aspectRatioFrameLayout = findViewById(R.id.stream_playback_aspect_ratio);

        streamPlayer = new StreamaxiaPlayer();
        streamPlayer.initStreamaxiaPlayer(surfaceView, aspectRatioFrameLayout, playbackStateTV, this, this, streamPlaybackURI);

        recyclerViewComment = findViewById(R.id.player_recyclerview_comments);
        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerViewComment.setLayoutManager(linearLayoutManager);

        aspectRatioFrameLayout.setOnClickListener(playPauseBtnListener);
        streamLiked = false;
        streamLikeIV.setImageResource(R.drawable.ic_like_border_white_24);
        streamLikesTV.setVisibility(View.VISIBLE);
        streamLikesTV.setText(String.valueOf(streamLikes));
        streamUsernameTV.setText(streamUsername);
        streamLikeIV.setOnClickListener(likeListener);
        postCommentTIL.setEndIconOnClickListener(submitCommentListener);
        showCommentsToggleBtn.setOnCheckedChangeListener(showCommentsListener);
        streamProfileIV.setOnClickListener(streamProfileListener);
        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener);

        getStreamerInfo();
        showAmountLikes();
        addCurrentViewer();
        showAmountViewers();
        hideComments();

//        TypedSource typedSource = TypedSource.Builder
//                .typedSource()
//                .src(streamPlaybackURL)
//                .type(SourceType.HLS)
//                .build();
//        SourceDescription sourceDescription = SourceDescription.Builder
//                .sourceDescription(typedSource)
//                .build();
//        Player player = playerView.getPlayer();
//        player.setSource(sourceDescription);
//        player.addEventListener(PlayerEventTypes.PLAY, playEventListener);
//        player.addEventListener(PlayerEventTypes.PAUSE, pauseEventListener);
//        player.addEventListener(PlayerEventTypes.CANPLAY, readyToPlayListener);
//        player.addEventListener(PlayerEventTypes.ERROR, streamErrorListener);
//        player.addEventListener(PlayerEventTypes.ENDED, streamEndedListener);
//        player.addEventListener(PlayerEventTypes.PLAYING, streamPlayingListener);

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

        initRTMPExoPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
        streamPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeCurrentViewer();
        streamPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeCurrentViewer();
        streamPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCurrentViewer();
        streamPlayer.stop();
    }

    private void getStreamerInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && !extras.isEmpty()) {
                streamUsername = extras.getString(KEY_STREAM_USERNAME);
                streamerUID = extras.getString(KEY_USER_ID);
                streamPlaybackURI = Uri.parse(extras.getString(KEY_STREAM_PLAYBACK_URL));
            }
        } else {
            Toast.makeText(this, R.string.player_stream_error, Toast.LENGTH_SHORT).show();
            proceedToMain();
        }
    }

    Runnable hide = new Runnable() {
        @Override
        public void run() {
            playPauseIV.setVisibility(View.GONE);
        }
    };

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

    private void hideComments() {
        recyclerViewComment.setVisibility(View.GONE);
    }

    private void showComments() {
        recyclerViewComment.setVisibility(View.VISIBLE);
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
        intent.putExtra(KEY_USERNAME, streamUsername);
        startActivity(intent);
    }

    private void addCurrentViewer() {
        streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int amountViewers = snapshot.getValue(Integer.class);
                            amountViewers++;
                            int finalAmountViewers = amountViewers;
                            streamersRef.child(streamerUID).child(KEY_STREAM_VIEWERS).setValue(amountViewers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    streamViewersTV.setText(String.valueOf(finalAmountViewers));
                                }
                            });
                        }
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
        streamLikes = 0;
        streamersRef.child(streamerUID).child(KEY_STREAM_LIKES)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            streamLikes = snapshot.getValue(Integer.class);
                        }
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
        if (currentUser != null) {
            usersRef.child(currentUser.getUid()).child(KEY_USERNAME)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.getValue(String.class);
                            Comment newComment = new Comment(currentUser.getUid(), username, commentBody);
                            streamersRef.child(streamerUID).child(KEY_COMMENTS).push().setValue(newComment);
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

//    private EventListener<CanPlayEvent> readyToPlayListener = new EventListener<CanPlayEvent>() {
//        @Override
//        public void handleEvent(CanPlayEvent canPlayEvent) {
//            playbackProgressBar.setVisibility(View.GONE);
//            playPauseIV.setImageResource(R.drawable.ic_play_white_24);
//            playPauseIV.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private EventListener<PlayEvent> playEventListener = new EventListener<PlayEvent>() {
//        @Override
//        public void handleEvent(PlayEvent playEvent) {
//            playPauseIV.setVisibility(View.GONE);
//            playbackProgressBar.setVisibility(View.GONE);
//            streamLiveIV.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private EventListener<PlayingEvent> streamPlayingListener = new EventListener<PlayingEvent>() {
//        @Override
//        public void handleEvent(PlayingEvent playingEvent) {
//            playPauseIV.setVisibility(View.GONE);
//            playbackProgressBar.setVisibility(View.GONE);
//            streamLiveIV.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private EventListener<PauseEvent> pauseEventListener = new EventListener<PauseEvent>() {
//        @Override
//        public void handleEvent(PauseEvent pauseEvent) {
//            playPauseIV.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private EventListener<ErrorEvent> streamErrorListener = new EventListener<ErrorEvent>() {
//        @Override
//        public void handleEvent(ErrorEvent errorEvent) {
//            removeCurrentViewer();
//            showStreamErrorDialog();
//        }
//    };
//
//    private EventListener<EndedEvent> streamEndedListener = new EventListener<EndedEvent>() {
//        @Override
//        public void handleEvent(EndedEvent endedEvent) {
//            playbackProgressBar.setVisibility(View.GONE);
//            streamLiveIV.setVisibility(View.INVISIBLE);
//            removeCurrentViewer();
//            showStreamEndedDialog();
//        }
//    };

    private void showStreamEndedDialog() {
        StreamEndedDialog streamEndedDialog = StreamEndedDialog.newInstance(streamerUID);
        Bundle data = new Bundle();
        data.putString(KEY_USER_ID, streamerUID);
        streamEndedDialog.setArguments(data);
        streamEndedDialog.show(getSupportFragmentManager(), "stream_ended");
    }

    private void showStreamErrorDialog() {
        StreamErrorDialog streamErrorDialog = new StreamErrorDialog();
        streamErrorDialog.show(getSupportFragmentManager(), "stream_error");
    }

    private void initRTMPExoPlayer() {
        streamPlayer.initStreamaxiaPlayer(
                surfaceView,
                aspectRatioFrameLayout,
                playbackStateTV,
                this,
                this,
                streamPlaybackURI);
    }

    @Override
    public void stateUNKNOWN() {
        showStreamErrorDialog();
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
        playPauseIV.setVisibility(View.GONE);
    }

    @Override
    public void stateIDLE() {
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
        playPauseIV.setVisibility(View.GONE);
    }

    @Override
    public void stateBUFFERING() {
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
        playPauseIV.setVisibility(View.GONE);
    }

    @Override
    public void statePREPARING() {
        streamLiveIV.setVisibility(View.INVISIBLE);
        playbackProgressBar.setVisibility(View.VISIBLE);
        playPauseIV.setVisibility(View.GONE);
    }

    @Override
    public void stateREADY() {
        playbackProgressBar.setVisibility(View.GONE);
        playbackStateTV.setVisibility(View.INVISIBLE);
        playPauseIV.setVisibility(View.VISIBLE);
    }

    @Override
    public void stateENDED() {
        playbackProgressBar.setVisibility(View.GONE);
        showStreamEndedDialog();
    }
}