package pack.jetminister.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import pack.jetminister.R;
import pack.jetminister.data.util.BroadcastLocation;

public class AskToStreamActivity extends AppCompatActivity {

    private TextInputLayout nameTIL , familyNameTIL , contentTIL , aboutYourselfTIL , moreTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_to_stream);

        nameTIL = findViewById(R.id.til_streamer_name);
        familyNameTIL = findViewById(R.id.til_streamer_family);
        contentTIL = findViewById(R.id.til_streamer_content);
        aboutYourselfTIL = findViewById(R.id.til_streamer_aboutyourself);
        moreTIL = findViewById(R.id.til_streamer_more);
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
                validateFamilyName(nameTIL.getEditText().getText().toString());
                validateContent(nameTIL.getEditText().getText().toString());
                validateAboutYourself(nameTIL.getEditText().getText().toString());
            }
        });
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