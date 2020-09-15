package pack.jetminister.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import pack.jetminister.R;

public class VideoTestActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView usernameText, likesText;
    private ImageView profileImage, likeImage, shareImage;

    private ActionBar toolbar;
    private boolean testBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_test);

        videoView = findViewById(R.id.video_Test_View);
        usernameText = findViewById(R.id.Username_Test_Video);
        likesText = findViewById(R.id.Amount_Likes_Textview);
        profileImage = findViewById(R.id.Profile_ImageView);
        likeImage = findViewById(R.id.Like_Imageview);
        shareImage = findViewById(R.id.Share_Imageview);

        toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.fragment_live);
        toolbar.hide();

        Intent intent = getIntent();

        testBoolean = false;

        likeImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    //        String imagePath = "android.resource://pack.jetminister/" + R.drawable.ic_baseline_favorite_border_24;
//        likeImage.setImageURI(Uri.parse(imagePath));
        if (intent != null){
            String username = intent.getStringExtra("username");
            likesText.setText("251");

            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        likeUnlike();

//                        String imagePath = "android.resource://pack.jetminister/" + R.drawable.ic_baseline_favorite_24;
//                        likeImage.setImageURI(Uri.parse(imagePath));
                }
            });

            MediaController m = new MediaController(this);
            videoView.setMediaController(m);

            switch(username) {
                case "DavidVideo":
                    // code block
                    usernameText.setText(username);

                    String pathDavid = "android.resource://pack.jetminister/" + R.raw.david_live;
                    videoView.setVideoURI(Uri.parse(pathDavid));
                    break;

                case "Hackermann":
                    // code block
                    usernameText.setText(username);

                    String pathDymas = "android.resource://pack.jetminister/" + R.raw.dymas_live;
                    videoView.setVideoURI(Uri.parse(pathDymas));
                    break;
                case "EricTv":
                    // code block
                    usernameText.setText(username);

                    String pathEric = "android.resource://pack.jetminister/" + R.raw.eric_minister;
                    videoView.setVideoURI(Uri.parse(pathEric));
                    break;
                case "Timothy.Tv":
                    // code block
                    usernameText.setText(username);

                    String pathTimothy = "android.resource://pack.jetminister/" + R.raw.timothy_live;
                    videoView.setVideoURI(Uri.parse(pathTimothy));
                    break;
                default:
                    // code block
                    usernameText.setText("ExtraUser");
            }

            videoView.start();
        }
    }

    private void likeUnlike() {
        if (testBoolean == false){
            likesText.setText("252");
            likeImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            testBoolean = true;
        } else {
            likesText.setText("251");
            likeImage.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            testBoolean = false;
        }

    }

}