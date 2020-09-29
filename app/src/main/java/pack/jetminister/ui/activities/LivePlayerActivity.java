package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pack.jetminister.R;
import pack.jetminister.data.Comment;
import pack.jetminister.data.LiveStream;
import pack.jetminister.data.User;
import pack.jetminister.ui.fragments.LiveFragment;
import pack.jetminister.ui.fragments.ProfileFragment;
import pack.jetminister.ui.util.adapter.AdminAdapter;
import pack.jetminister.ui.util.adapter.CommentAdapter;

import static pack.jetminister.data.Comment.KEY_COMMENTS;
import static pack.jetminister.data.LiveStream.KEY_LIVE_STREAMS;
import static pack.jetminister.data.LiveStream.KEY_STREAM_LIKES;
import static pack.jetminister.data.LiveStream.KEY_STREAM_USERNAME;
import static pack.jetminister.data.LiveStream.KEY_STREAM_VIEWERS;
import static pack.jetminister.data.User.KEY_USERNAME;
import static pack.jetminister.data.User.KEY_USERS;
import static pack.jetminister.data.User.KEY_USER_ID;
import static pack.jetminister.ui.util.adapter.LivePictureAdapter.KEY_URI;

public class LivePlayerActivity extends AppCompatActivity implements StreamaxiaPlayerState {
    private static final String TAG = "LivePlayerActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(KEY_USERS);
    private DatabaseReference streamersRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS);
    private TextView streamUsernameTV, streamLikesTV, streamViewersTV, playbackStateTV;
    private ProgressBar playbackProgressBar;
    private ImageView streamLiveIV, streamProfileIV, streamLikeIV, streamShareIV, playPauseIV;
    private SurfaceView playbackSurfaceView;
    private AspectRatioFrameLayout playbackAspectRatioLayout;
    private EditText postCommentET;
    private RecyclerView recyclerViewComment;

    private StreamaxiaPlayer streamPlayer = new StreamaxiaPlayer();

    private boolean streamLiked;
    private String streamUsername;
    private Uri streamPlaybackURL;
    private int streamLikes;
    private String streamUID;

    private CommentAdapter mAdapter;
    private List<Comment> mComments;

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
                playbackProgressBar.setVisibility(View.VISIBLE);
            } else {
                playPauseIV.setVisibility(View.GONE);
                playbackProgressBar.setVisibility(View.GONE);
                int STREAM_TYPE = StreamaxiaPlayer.TYPE_HLS;
                streamPlayer.play(streamPlaybackURL, STREAM_TYPE);
            }
        }
    };

    private View.OnClickListener streamProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LivePlayerActivity.this , StreamerProfileActivity.class);
            intent.putExtra("streamerID", streamUID);
            startActivity(intent);
        }
    };


    private TextView.OnEditorActionListener postCommentListener = new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // Identifier of the action. This will be either the identifier you supplied,
            // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
            if (actionId == EditorInfo.IME_ACTION_DONE
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String commentBody = postCommentET.getText().toString().trim();
                addCommentToStream(commentBody);
                postCommentET.getText().clear();
                return true;
            }

            return false;
        }
    };

    private KeyboardVisibilityEventListener keyboardVisibilityListener = new KeyboardVisibilityEventListener() {
        @Override
        public void onVisibilityChanged(boolean isOpen) {
            if (isOpen){
                streamLikeIV.setVisibility(View.INVISIBLE);
                streamLikesTV.setVisibility(View.INVISIBLE);
                streamShareIV.setVisibility(View.INVISIBLE);
                streamProfileIV.setVisibility(View.INVISIBLE);
            }else{
                streamLikeIV.setVisibility(View.VISIBLE);
                streamLikesTV.setVisibility(View.VISIBLE);
                streamShareIV.setVisibility(View.VISIBLE);
                streamProfileIV.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnClickListener submitCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!postCommentET.getText().toString().trim().isEmpty()){
                String commentBody = postCommentET.getText().toString().trim();
                addCommentToStream(commentBody);
                postCommentET.getText().clear();
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

        streamUsernameTV = findViewById(R.id.player_tv_username);
        streamViewersTV = findViewById(R.id.player_tv_watching);
        streamLikesTV = findViewById(R.id.player_tv_amount_likes);
        playbackStateTV = findViewById(R.id.tv_live_player_state);
        streamLiveIV = findViewById(R.id.player_iv_live);
        streamProfileIV = findViewById(R.id.player_iv_profile);
        streamLikeIV = findViewById(R.id.player_iv_like);
        streamShareIV = findViewById(R.id.player_iv_share);
        playPauseIV = findViewById(R.id.player_iv_play);
        playbackSurfaceView = findViewById(R.id.player_surface_view);
        playbackAspectRatioLayout = findViewById(R.id.player_aspect_ratio);
        playbackProgressBar = findViewById(R.id.player_progress_bar);
        postCommentET = findViewById(R.id.ET_comment_here);
        submitCommentIV = findViewById(R.id.player_iv_comment_submit);

        getStreamerInfo();
        showAmountLikes();
        addCurrentViewer();
        showAmountViewers();

        streamLiked = false;
        streamLikeIV.setImageResource(R.drawable.ic_like_border_white_24);
        streamLikesTV.setVisibility(View.VISIBLE);
        streamLikesTV.setText(String.valueOf(streamLikes));
        streamUsernameTV.setText(streamUsername);
        streamLikeIV.setOnClickListener(likeListener);
        playbackAspectRatioLayout.setOnClickListener(playPauseListener);
        postCommentET.setOnEditorActionListener(postCommentListener);
        submitCommentIV.setOnClickListener(submitCommentListener);
        streamProfileIV.setOnClickListener(streamProfileListener);

        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener);

        recyclerViewComment = findViewById(R.id.recyclerview_comments);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));

        mComments = new ArrayList<>();
        DatabaseReference streamersDatabaseRef = FirebaseDatabase.getInstance().getReference(KEY_LIVE_STREAMS).child(streamUID).child(KEY_COMMENTS);

        streamersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mComments.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    mComments.add(comment);
                    mAdapter = new CommentAdapter(LivePlayerActivity.this, mComments);
                    recyclerViewComment.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        initRTMPExoPlayer();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initRTMPExoPlayer() {
        streamPlayer.initStreamaxiaPlayer(
                playbackSurfaceView,
                playbackAspectRatioLayout,
                playbackStateTV,
                this,
                this,
                streamPlaybackURL);
    }

    private void getStreamerInfo() {
        Bundle extras = getIntent().getExtras();
        streamUsername = extras.getString(KEY_STREAM_USERNAME);
        streamPlaybackURL = Uri.parse(extras.getString(KEY_URI));
        streamUID = extras.getString(KEY_USER_ID);
    }

    private void addCurrentViewer(){
        streamersRef.child(streamUID).child(KEY_STREAM_VIEWERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amountViewers = snapshot.getValue(Integer.class);
                        amountViewers++;
                        streamViewersTV.setText(String.valueOf(amountViewers));
                        streamersRef.child(streamUID).child(KEY_STREAM_VIEWERS).setValue(amountViewers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void showAmountViewers() {
        streamersRef.child(streamUID).child(KEY_STREAM_VIEWERS)
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
        streamersRef.child(streamUID).child(KEY_STREAM_LIKES)
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
            streamersRef.child(streamUID).child(KEY_STREAM_LIKES).setValue(streamLikes);
            streamLikeIV.setImageResource(R.drawable.ic_like_fill_white_24);
            streamLiked = true;
        } else {
            streamLikes--;
            streamersRef.child(streamUID).child(KEY_STREAM_LIKES).setValue(streamLikes);
            streamLikeIV.setImageResource(R.drawable.ic_like_border_white_24);
            streamLiked = false;
        }
    }

    private void addCommentToStream(String commentBody) {
        long commentID = System.currentTimeMillis();
        usersRef.child(currentUser.getUid()).child(KEY_USERNAME)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                Comment newComment = new Comment(currentUser.getUid(), username, commentBody);
                streamersRef.child(streamUID).child(KEY_COMMENTS).child(String.valueOf(commentID)).setValue(newComment);
                Log.d(TAG, "new comment: " + newComment.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void stateUNKNOWN() {
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stateIDLE() {
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void stateBUFFERING() {
        playbackProgressBar.setVisibility(View.VISIBLE);
        streamLiveIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void statePREPARING() {
        streamLiveIV.setVisibility(View.INVISIBLE);
        playbackProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stateREADY() {
        playbackProgressBar.setVisibility(View.GONE);
        playbackStateTV.setVisibility(View.INVISIBLE);
        playPauseIV.setImageResource(R.drawable.ic_play_white_24);
    }

    @Override
    public void stateENDED() {
        playbackProgressBar.setVisibility(View.GONE);
        streamLiveIV.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        streamPlayer.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamPlayer.stop();
    }
}