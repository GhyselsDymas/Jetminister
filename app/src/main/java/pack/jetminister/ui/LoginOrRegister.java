package pack.jetminister.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.validators.EmailValidator;
import pack.jetminister.ui.util.validators.PasswordValidator;
import pack.jetminister.ui.util.validators.UsernameValidator;

public class LoginOrRegister extends AppCompatActivity {

    private static final String TAG = "LoginOrRegister";

    //Keys for the shared preferences
    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_EMAIL = "email";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_THEME = "theme";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private static final String SHARED_PREFS_STREAMER = "streamer";
    private static final String URI_JETMINISTER = "https://jetminister.com/";

    //get an instance of Firebase and a reference to the collection
    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = rootNode.getReference("users");

    private TextInputLayout usernameLoginTIL, passwordLoginTIL, emailRegisterTIL, usernameRegisterTIL, passwordRegisterTIL, passwordConfirmRegisterTIL;
    private CheckBox termsConditionCB;

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginUser();
        }
    };
    View.OnClickListener termsConditionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent termsConditionsIntent = new Intent(Intent.ACTION_VIEW);
            termsConditionsIntent.setData(Uri.parse(URI_JETMINISTER));
        }
    };

    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            registerUser();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        usernameLoginTIL = findViewById(R.id.til_username);
        passwordLoginTIL = findViewById(R.id.til_password);
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(loginListener);

        usernameRegisterTIL = findViewById(R.id.til_register_username);
        passwordRegisterTIL = findViewById(R.id.til_register_password);
        passwordConfirmRegisterTIL = findViewById(R.id.til_register_password_confirm);
        termsConditionCB = findViewById(R.id.action_checkbox_terms_conditions);
        TextView termsConditionsTV = findViewById(R.id.tv_register_terms_cond);
        termsConditionsTV.setPaintFlags(termsConditionsTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsConditionsTV.setOnClickListener(termsConditionsListener);
        Button registerBtn = findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(registerListener);
    }

    private void registerUser() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CharSequence confirmPasswordError = getResources().getString(R.string.register_error_confirmpassword);
                //get values from the text fields
                String newEmail = emailRegisterTIL.getEditText().getText().toString().trim();
                String newUsername = usernameRegisterTIL.getEditText().getText().toString().trim();
                String newPassword = passwordRegisterTIL.getEditText().getText().toString();
                String newConfirmPassword = passwordConfirmRegisterTIL.getEditText().getText().toString();
                //check if the password fields match AND the email address is valid AND there is no duplicate username AND the tersms and conditions have been accepted
                if (!validatePassword(newPassword)
                        | (confirmPasswordMatch(newPassword, newConfirmPassword))
                        | validateEmail(newEmail)
                        | isDuplicate(dataSnapshot, newUsername)
                        | termsConditionCB.isChecked()) {
                } else {
                    //create new user with values from the textfields
                    User newUser = new User(newUsername, newPassword, newEmail);
                    //create new entry in database by username
                    usersRef.child(newUsername).setValue(newUser);
                    Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    saveUserInfo(dataSnapshot, newUsername);
                    proceedToMain();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean validateEmail(String email) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);
        CharSequence invalidEmailError = getResources().getString(R.string.register_error_email_invalid);

        if (email.isEmpty()) {
            emailRegisterTIL.setError(emptyFieldError);
            return false;
        } else if (!EmailValidator.validate(email)) {
            emailRegisterTIL.setError(invalidEmailError);
        } else {
            emailRegisterTIL.setError(null);
            emailRegisterTIL.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword(String password) {
        CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);
        CharSequence invalidPasswordError = getResources().getString(R.string.register_error_password_invalid);

        if (password.isEmpty()) {
            passwordRegisterTIL.setError(emptyFieldError);
            return false;
        } else if (!PasswordValidator.validate(password)) {
            passwordRegisterTIL.setError(invalidPasswordError);
            return false;
        } else {
            passwordRegisterTIL.setError(null);
            passwordRegisterTIL.setErrorEnabled(false);
            return true;
        }
    }

    private boolean confirmPasswordMatch(String password, String confirmPassword) {
        CharSequence confirmPasswordError = getResources().getString(R.string.register_error_confirmpassword);
        if (!password.equals(confirmPassword)) {
            passwordConfirmRegisterTIL.setError(confirmPasswordError);
            return false;
        }
        passwordConfirmRegisterTIL.setError(null);
        passwordConfirmRegisterTIL.setErrorEnabled(false);
        return true;
    }


    private boolean isDuplicate(DataSnapshot dataSnapshot, String newUsername) {
        CharSequence duplicateUsernameError = getResources().getString(R.string.register_error_username_duplicate);
        //loop over the database, convert each entry to User and get username to check for duplicates
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if (snapshot.exists()) {
                User checkUser = snapshot.getValue(User.class);
                if (checkUser != null) {
                    if (newUsername.equals(checkUser.getUsername())) {
                        usernameRegisterTIL.setError(duplicateUsernameError);
                        return false;
                    }
                }
            }
        }
        usernameRegisterTIL.setError(null);
        usernameRegisterTIL.setErrorEnabled(false);
        return true;
    }

    private void loginUser() {
        final String inputUsername = usernameLoginTIL.getEditText().getText().toString().trim();
        final String inputPassword = passwordLoginTIL.getEditText().getText().toString();
        Query checkUserQuery = usersRef.orderByChild("username").equalTo(inputUsername);
        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (!isRegisteredUser(userSnapshot)) {
                    Toast.makeText(LoginOrRegister.this, "Not a registered user", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isCorrectPassword(userSnapshot, inputUsername, inputPassword)) {
                    Toast.makeText(LoginOrRegister.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUserInfo(userSnapshot, inputUsername);
                proceedToMain();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean isRegisteredUser(DataSnapshot snapshot) {
        CharSequence unknownUserError = getResources().getString(R.string.login_error_user_unknown);
        if (!snapshot.exists()) {
            usernameLoginTIL.setError(unknownUserError);
            return false;
        } else {
            usernameLoginTIL.setError(null);
            usernameLoginTIL.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isCorrectPassword(DataSnapshot snapshot, String username, String password) {
        CharSequence wrongPasswordError = getResources().getString(R.string.login_error_wrong_password);
        String passwordFromDatabase = snapshot.child(username).child("password").getValue(String.class);

        if (passwordFromDatabase == null || !passwordFromDatabase.equals(password)) {
            passwordLoginTIL.setError(wrongPasswordError);
            return false;
        } else {
            passwordLoginTIL.setError(null);
            passwordLoginTIL.setErrorEnabled(false);
            return true;
        }
    }

    private void saveUserInfo(DataSnapshot snapshot, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFS_USERNAME, snapshot.child(username).child("username").getValue(String.class));
        editor.putString(SHARED_PREFS_EMAIL, snapshot.child(username).child("email").getValue(String.class));
        editor.putString(SHARED_PREFS_DESCRIPTION, snapshot.child(username).child("description").getValue(String.class));
        editor.putString(SHARED_PREFS_THEME, snapshot.child(username).child("theme").getValue(String.class));
        editor.putString(SHARED_PREFS_IMAGE_URL, snapshot.child(username).child("imageURL").getValue(String.class));
        editor.putBoolean(SHARED_PREFS_STREAMER, snapshot.child(username).child("streamer").getValue(Boolean.class));
        editor.apply();
    }

    private void proceedToMain() {
//        String usernameFromDatabase = snapshot.child(username).child("username").getValue(String.class);
//        String passwordFromDatabase = snapshot.child(username).child("password").getValue(String.class);
//        String emailFromDatabase = snapshot.child(username).child("email").getValue(String.class);
//        String descriptionFromDatabase = snapshot.child(username).child("description").getValue(String.class);
//        String themeFromDatabase = snapshot.child(username).child("theme").getValue(String.class);
//        String imageURLFromDatabase = snapshot.child(username).child("imageURL").getValue(String.class);
//        boolean streamerFromDatabase = snapshot.child(username).child("streamer").getValue(Boolean.class);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("username", usernameFromDatabase);
//        intent.putExtra("password", passwordFromDatabase);
//        intent.putExtra("email", emailFromDatabase);
//        intent.putExtra("description", descriptionFromDatabase);
//        intent.putExtra("theme", themeFromDatabase);
//        intent.putExtra("imageURL", imageURLFromDatabase);
//        intent.putExtra("streamer", streamerFromDatabase);
        startActivity(intent);
    }

}