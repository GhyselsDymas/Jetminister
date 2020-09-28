package pack.jetminister.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.util.BroadcastLocation;

public class AskToStreamActivity extends AppCompatActivity {

    private TextInputLayout nameTIL , familyNameTIL , contentTIL , aboutYourselfTIL , moreTIL;
    private Button confirmButton;
    private Spinner spinnerServerLocation , spinnerAudience;
    private BroadcastLocation broadcastLocation;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    private final String[] REQUEST_EMAIL = {"ghyselsdymas@gmail.com"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_to_stream);

        nameTIL = findViewById(R.id.til_streamer_name);
        familyNameTIL = findViewById(R.id.til_streamer_family);
        contentTIL = findViewById(R.id.til_streamer_content);
        aboutYourselfTIL = findViewById(R.id.til_streamer_aboutyourself);
        moreTIL = findViewById(R.id.til_streamer_more);
        confirmButton = findViewById(R.id.btn_ask_to_stream);
        spinnerServerLocation = findViewById(R.id.spinner_server_location);
        spinnerAudience = findViewById(R.id.spinner_audience);
        Button confirmButton = findViewById(R.id.btn_ask_to_stream);
        Spinner spinnerServerLocation = findViewById(R.id.spinner_server_location);

        List<String> list = BroadcastLocation.getBroadcastLocationList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServerLocation.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateName(nameTIL.getEditText().getText().toString());
                validateFamilyName(familyNameTIL.getEditText().getText().toString());
                validateContent(contentTIL.getEditText().getText().toString());
                validateAboutYourself(aboutYourselfTIL.getEditText().getText().toString());

                Integer location = spinnerServerLocation.getSelectedItemPosition();

                List<String> myArrayList = Arrays.asList(getResources().getStringArray(R.array.locationServer));
                usersRef.child(currentUser.getUid()).child("location").setValue(myArrayList.get(location));

                sendMail();
            }
        });
    }

    private void sendMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, REQUEST_EMAIL);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reqeust to become streamer by " + nameTIL.getEditText().getText().toString() + " " + familyNameTIL.getEditText().getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "What type of content will you share with the viewers from your stream? \n\n\b\b"
            + contentTIL.getEditText().getText().toString() +
                "\n\n\nWhat is the target audience of your stream? \n\n\b\b" +
                spinnerAudience.getSelectedItem().toString() +
                "\n\n\nTell us something about yourself.\n\n\b\b" +
                aboutYourselfTIL.getEditText().getText().toString() +
                "\n\n\nIs there anything else that we should know about. \n\n\b\b" +
                moreTIL.getEditText().getText().toString()
        );

        if (emailIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose your email application you want to use"));
        }
    }


    private boolean validateName(String name) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);

        if (name.isEmpty()) {
            nameTIL.setError(emptyFieldError);
            return false;
        } else {
            nameTIL.setError(null);
            nameTIL.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFamilyName(String familyName) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);

        if (familyName.isEmpty()) {
            familyNameTIL.setError(emptyFieldError);
            return false;
        } else {
            familyNameTIL.setError(null);
            familyNameTIL.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateContent(String content) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);

        if (content.isEmpty()) {
            contentTIL.setError(emptyFieldError);
            return false;
        } else {
            contentTIL.setError(null);
            contentTIL.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAboutYourself(String aboutYourself) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);

        if (aboutYourself.isEmpty()) {
            aboutYourselfTIL.setError(emptyFieldError);
            return false;
        } else {
            aboutYourselfTIL.setError(null);
            aboutYourselfTIL.setErrorEnabled(false);
            return true;
        }
    }
}