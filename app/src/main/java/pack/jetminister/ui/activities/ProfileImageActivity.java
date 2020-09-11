package pack.jetminister.ui.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
    private Button chooseImageBtn;
    private Button uploadImageBtn;
    private Button cancelBtn;
    private ImageView previewIV;
    private ProgressBar mProgressBar;
    private Uri mImageUri;

    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
    private StorageTask mUploadToStorageTask;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_page);

        chooseImageBtn = findViewById(R.id.button_choose_image);
        uploadImageBtn = findViewById(R.id.button_upload);
        cancelBtn = findViewById(R.id.button_cancel);
        previewIV = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

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
                    uploadImageToStorage();
                }
            }
        });
    }

    private void uploadImageToStorage() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uID = currentUser.getUid();
        final DatabaseReference currentUserDatabaseRef = usersDatabaseRef.child(uID);
        final String newImageFilename = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
        StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("images/" + newImageFilename);
        if (mImageUri != null) {
            imageStorageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            UserProfileChangeRequest newProfile = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(uri)
                                                    .build();
                                            currentUser.updateProfile(newProfile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            currentUserDatabaseRef.child("imageFilename").setValue(newImageFilename);
                                                            currentUserDatabaseRef.child("imageURL").setValue(uri.toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                proceedToMain();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ProfileImageActivity.this, "Could not update database entry for imageUrl", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            Toast.makeText(ProfileImageActivity.this, R.string.profile_image_upload_success, Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(ProfileImageActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void proceedToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}