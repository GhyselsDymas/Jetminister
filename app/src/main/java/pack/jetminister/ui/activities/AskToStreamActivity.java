package pack.jetminister.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import pack.jetminister.R;

public class AskToStreamActivity extends AppCompatActivity {

    private TextInputLayout nameTIL , familyNameTIL , contentTIL , aboutYourselfTIL , moreTIL;
    private Button confirmButton;

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