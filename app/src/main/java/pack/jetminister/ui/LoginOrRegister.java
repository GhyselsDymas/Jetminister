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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = rootNode.getReference("users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private TextInputLayout emailLoginTIL, passwordLoginTIL, emailRegisterTIL, usernameRegisterTIL, passwordRegisterTIL, passwordConfirmRegisterTIL;
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

        emailLoginTIL = findViewById(R.id.til_login_email);
        passwordLoginTIL = findViewById(R.id.til_login_password);
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(loginListener);

        emailRegisterTIL = findViewById(R.id.til_register_email);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){

        }
    }

    private void registerUser() {
        final String newEmail = emailRegisterTIL.getEditText().getText().toString().trim();
        final String newUsername = usernameRegisterTIL.getEditText().getText().toString().trim();
        final String newPassword = passwordRegisterTIL.getEditText().getText().toString();
        final String newConfirmPassword = passwordConfirmRegisterTIL.getEditText().getText().toString();
        authenticateRegisteredUser(newEmail, newPassword);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CharSequence confirmPasswordError = getResources().getString(R.string.register_error_confirmpassword);
                //get values from the text fields

                //check if the password fields match AND the email address is valid AND there is no duplicate username AND the tersms and conditions have been accepted
                if (    !validatePassword(newPassword)
                        | (confirmPasswordMatch(newPassword, newConfirmPassword))
                        | validateEmail(newEmail)
                        | isDuplicate(dataSnapshot, newUsername)
                        | !checkedTermsConditions(termsConditionCB)) {
                    Toast.makeText(LoginOrRegister.this, R.string.register_error, Toast.LENGTH_SHORT).show();
                }
                else {

                    //create new user with values from the textfields
                    User newUser = new User(newUsername, newPassword, newEmail);
                    //create new entry in database by username
                    usersRef.child(newUsername)     .setValue(newUser);
                    Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    saveUserInfo(dataSnapshot, newEmail);
                    proceedToMain();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void authenticateRegisteredUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginOrRegister.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginOrRegister.this, R.string.register_authentication_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "authentication: " + task.getException().getMessage());
                }
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

    private boolean checkedTermsConditions(CheckBox checkBox){
        if (!checkBox.isChecked()){
            Toast.makeText(this, R.string.cb_terms_conditions, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        final String inputEmail = emailLoginTIL.getEditText().getText().toString().trim();
        final String inputPassword = passwordLoginTIL.getEditText().getText().toString();

        authenticateUserLogin(inputEmail, inputPassword);

        Query checkUserQuery = usersRef.orderByChild("email").equalTo(inputEmail);
        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (!isRegisteredUser(userSnapshot)) {
                    Toast.makeText(LoginOrRegister.this, "Not a registered user", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isCorrectPassword(userSnapshot, inputEmail, inputPassword)) {
                    Toast.makeText(LoginOrRegister.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUserInfo(userSnapshot, inputEmail);
                proceedToMain();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void authenticateUserLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginOrRegister.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginOrRegister.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isRegisteredUser(DataSnapshot snapshot) {
        CharSequence unknownUserError = getResources().getString(R.string.login_error_user_unknown);
        if (!snapshot.exists()) {
            emailLoginTIL.setError(unknownUserError);
            return false;
        } else {
            emailLoginTIL.setError(null);
            emailLoginTIL.setErrorEnabled(false);
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

    private void saveUserInfo(DataSnapshot snapshot, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFS_USERNAME, snapshot.child(email).child("username").getValue(String.class));
        editor.putString(SHARED_PREFS_EMAIL, snapshot.child(email).child("email").getValue(String.class));
        editor.putString(SHARED_PREFS_DESCRIPTION, snapshot.child(email).child("description").getValue(String.class));
        editor.putString(SHARED_PREFS_THEME, snapshot.child(email).child("theme").getValue(String.class));
        editor.putString(SHARED_PREFS_IMAGE_URL, snapshot.child(email).child("imageURL").getValue(String.class));
        editor.putBoolean(SHARED_PREFS_STREAMER, snapshot.child(email).child("streamer").getValue(Boolean.class));
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

        //clear all activities from stack so when user hits 'back' on phone, he will not return to loginactivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}