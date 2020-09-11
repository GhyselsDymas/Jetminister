package pack.jetminister.ui.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
import pack.jetminister.ui.fragments.ProfileFragment;

public class ProfileImageActivity extends AppCompatActivity {

    private static final String TAG = "ProfileImageActivity";

    public ProfileImageActivity() {
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button chooseImageBtn;
    private Button uploadImageBtn;
    private Button cancelBtn;
    private ImageView previewIV;
    private ProgressBar uploadImagePB;
    private Uri mImageUri;

    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
    private StorageTask mUploadToStorageTask;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //return to user profile screen
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProfileFragment destinationFragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, destinationFragment).commit();
        }
    };

    View.OnClickListener chooseImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseFromImagePicker();
        }
    };

    View.OnClickListener uploadImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //check for running tasks to prevent concurrent/duplicate uploads, display specific error message
            if (mUploadToStorageTask != null && mUploadToStorageTask.isInProgress()) {
                Toast.makeText(ProfileImageActivity.this, R.string.profile_image_error_duplicate, Toast.LENGTH_SHORT).show();
            } else {
                uploadImageToStorage();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        chooseImageBtn = findViewById(R.id.btn_choose_image);
        uploadImageBtn = findViewById(R.id.btn_upload);
        cancelBtn = findViewById(R.id.btn_cancel);
        previewIV = findViewById(R.id.iv_image_preview);
//        mProgressBar = findViewById(R.id.progress_bar);
        uploadImagePB = findViewById(R.id.progressbar_image_upload);
        cancelBtn.setOnClickListener(cancelListener);
        chooseImageBtn.setOnClickListener(chooseImageListener);
        uploadImageBtn.setOnClickListener(uploadImageListener);
    }

    private void uploadImageToStorage() {
        //check if file chooser returned an image
        if (mImageUri != null) {
            //create unique filename for image using timestamp and file extension
            final String newImageFilename = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            //make reference to database using unique ID from logged in firebase user and unique filename
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            final String uID = currentUser.getUid();
            final DatabaseReference currentUserDatabaseRef = usersDatabaseRef.child(uID);
            StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("images/" + newImageFilename);
            //set visibility of progress bar
            uploadImagePB.setVisibility(View.VISIBLE);
            //upload image to storage
            imageStorageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            //get the downloadURL from storage
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            //update the profile image of logged in firebase user
                                            UserProfileChangeRequest newProfile = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(uri)
                                                    .build();
                                            currentUser.updateProfile(newProfile)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //update the user's database entries for image filename and imageURL
                                                            currentUserDatabaseRef.child("imageFilename").setValue(newImageFilename);
                                                            currentUserDatabaseRef.child("imageURL").setValue(uri.toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                uploadImagePB.setVisibility(View.GONE);
                                                                                proceedToMain();
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "Could not update database entry for imageUrl");
                                                                }
                                                            });
                                                            Toast.makeText(ProfileImageActivity.this, R.string.profile_image_upload_success, Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Profile update failed");
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.getMessage());
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