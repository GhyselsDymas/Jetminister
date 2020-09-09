package pack.jetminister.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import pack.jetminister.R;

public class ProfileImageActivity extends AppCompatActivity {

    private static final String TAG = "ProfileImageActivity";

    public ProfileImageActivity() {
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private static final String SHARED_PREFS_IMAGE_FILENAME = "imageFilename";
    private Button chooseImageBtn;
    private Button uploadImageBtn;
    private Button cancelBtn;
    private ImageView previewIV;
    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadToStorageTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_page);

        chooseImageBtn = findViewById(R.id.button_choose_image);
        uploadImageBtn = findViewById(R.id.button_upload);
        cancelBtn = findViewById(R.id.button_cancel);
        previewIV = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromImagePicker();
            }
        });
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for running tasks to prevent duplicate uploads
                if (mUploadToStorageTask != null && mUploadToStorageTask.isInProgress()) {
                    Toast.makeText(ProfileImageActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

    }

    private void chooseFromImagePicker() {
        //request access to and open Android's image picker
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //if access was granted and an image was chosen, get the data from it
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && intent != null && intent.getData() != null) {
            mImageUri = intent.getData();
            //load image into preview
            Picasso.get().load(mImageUri).into(previewIV);
        }
    }

    private void uploadFile() {
        //check if an image was returned from image picker
        if (mImageUri != null) {
            //upload will store uri into user's db entry, so must get username from SharedPreferences
            final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final String username = sharedPreferences.getString(SHARED_PREFS_USERNAME, null);

            //make unique filename for storage with timestamp, and append file extension
            final String newImageFilename = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            //make storage reference using the new filename
            final StorageReference newImageReference = mStorageRef.child(newImageFilename);
            //put the data from the image into storage using new reference
            mUploadToStorageTask = newImageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //handle the upload using a progressbar for user feedback
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            //start a task to get the image's downloadURL from storage and save it in user's sharedpreferences
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadURI = uri.toString();
                                    editor.putString(SHARED_PREFS_IMAGE_URL, downloadURI);
                                    editor.apply();
                                    //store the downloadURL in the user's database entry
                                    mDatabaseRef.child(username).child("imageURL").setValue(downloadURI);
                                    //make database query for user entry
                                    Query checkUserQuery = mDatabaseRef.orderByChild("username").equalTo(username);
                                    checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            //check if user has entry
                                            if(snapshot.exists()){
                                                //get the old filename from database
                                                String oldFilename = snapshot.child("imageFilename").getValue().toString();
                                                StorageReference oldImageReference = mStorageRef.child(oldFilename);
                                                //delete the old file from storage
                                                oldImageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //put new image in storage with new filename
                                                        mDatabaseRef.child(username).child("imageFilename").setValue(newImageFilename);
                                                        Toast.makeText(ProfileImageActivity.this, R.string.profile_image_upload_success, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, R.string.profile_image_error_none_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}