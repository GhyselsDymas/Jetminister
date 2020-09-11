package pack.jetminister.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.validators.EmailValidator;
import pack.jetminister.ui.util.validators.PasswordValidator;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginOrRegister";
    private static final String UNIQUE_USERNAME = "Input username is unique";
    private static final String URI_JETMINISTER = "https://jetminister.com/";

    //get an instance of Firebase and a reference to the collection
    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
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
        //check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            //make sure user does not return to this activity when hitting 'back'
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void registerUser() {
        final String usernameFromInput = usernameRegisterTIL.getEditText().getText().toString().trim();
        //if username is valid, check if it already exists in database
        if (validateUsername(usernameFromInput)) {
            isDuplicateUsername(usernameFromInput);
        }
    }

    private void isDuplicateUsername(final String usernameFromInput) {
        final CharSequence duplicateUsernameError = getResources().getString(R.string.register_error_username_duplicate);
        //make reference to users in database
        usersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //initialise temporary unique String
                String tempCheck = UNIQUE_USERNAME;
                //loop opver user objects in database
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.exists() && userSnapshot.child("username").exists()) {
                        //get the String value of the username field
                        String snapshotUsername = userSnapshot.child("username").getValue(String.class);
                        if (usernameFromInput.equals(snapshotUsername)) {
                            //if user input equals username already in database, assign username to temporary string
                            tempCheck = snapshotUsername;
                        }
                    }
                }
                //if temp string is still unique, no duplicate was found in database
                if (tempCheck.equals(UNIQUE_USERNAME)) {
                    usernameRegisterTIL.setError(null);
                    usernameRegisterTIL.setErrorEnabled(false);
                    //proceed with authentication
                    authenticateRegisteredUser(usernameFromInput);
                } else {
                    //temp string is no longer unique, show error
                    usernameRegisterTIL.setError(duplicateUsernameError);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean validateUsername(final String usernameFromInput) {
        final CharSequence emptyFieldError = getResources().getString(R.string.register_error_empty_field);
        final CharSequence whitespaceUsernameError = getResources().getString(R.string.register_error_username_whitespace);
        final CharSequence characterLimitUsernameError = getResources().getString(R.string.register_error_username_limit);

        if (usernameFromInput.isEmpty()) {
            usernameRegisterTIL.setError(emptyFieldError);
            return false;
        } else if (usernameFromInput.length() > 16) {
            usernameRegisterTIL.setError(characterLimitUsernameError);
            return false;
        } else if (usernameFromInput.contains(" ")) {
            usernameRegisterTIL.setError(whitespaceUsernameError);
            return false;
        } else {
            usernameRegisterTIL.setError(null);
            usernameRegisterTIL.setErrorEnabled(false);
            return true;
        }
    }

    private void authenticateRegisteredUser(final String usernameFromInput) {
        final String emailFromInput = emailRegisterTIL.getEditText().getText().toString().trim();
        final String passwordFromInput = passwordRegisterTIL.getEditText().getText().toString();
        final String newConfirmPassword = passwordConfirmRegisterTIL.getEditText().getText().toString();
        boolean checkTermsConditions = termsConditionCB.isChecked();
        //check if email and password are valid, password is confirmed and terms and conditions are checked
        if (validateEmail(emailFromInput) &&
                validatePassword(passwordFromInput) &&
                confirmPasswordMatch(passwordFromInput, newConfirmPassword) &&
                confirmTermsConditions(checkTermsConditions)) {
            //register with firebase Auth
            mAuth.createUserWithEmailAndPassword(emailFromInput, passwordFromInput)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //create new User object to store into firebase Database
                                User newUser = new User(emailFromInput, passwordFromInput);
                                newUser.setUsername(usernameFromInput);
                                //add user to database
                                addUserToDatabase(newUser);
                                proceedToMain();
                            //if email is already registered, display specific error message
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                CharSequence duplicateEmailError = getResources().getString(R.string.register_error_email_duplicate);
                                emailRegisterTIL.setError(duplicateEmailError);
                            //if something else went wrong, display generic error message
                            } else {
                                Log.d(TAG, task.getException().getMessage());
                                emailRegisterTIL.getEditText().requestFocus();
                                Toast.makeText(LoginRegisterActivity.this, R.string.register_authentication_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserToDatabase(User newUser) {
        //check if registered user is logged in and retrieve unique ID
        if (mAuth.getCurrentUser() != null) {
            final String newUID = mAuth.getCurrentUser().getUid();
            //create new database entry with unique ID as key
            usersDatabaseRef.child(newUID).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginRegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, task.getException().getMessage());
                    }
                }
            });
        } else {
            Log.d(TAG, String.valueOf(R.string.register_authentication_error));
        }
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

    private boolean confirmTermsConditions(boolean termsConditions) {
        if (!termsConditions) {
            Toast.makeText(this, R.string.register_error_termsconditions_unchecked, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        final String inputEmail = emailLoginTIL.getEditText().getText().toString().trim();
        final String inputPassword = passwordLoginTIL.getEditText().getText().toString();
        //log user into firebase Auth
        mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            proceedToMain();
                            Toast.makeText(LoginRegisterActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        //if user enters email that does not exist, display specific error message
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            CharSequence unknownUserError = getResources().getString(R.string.login_error_email_unknown);
                            emailLoginTIL.setError(unknownUserError);
                        //if user enters password that does not correspond to the email, display specific error message
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            CharSequence wrongPasswordError = getResources().getString(R.string.login_error_wrong_password);
                            passwordLoginTIL.setError(wrongPasswordError);
                        //if something else went wrong, display generic error message
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                            Toast.makeText(LoginRegisterActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void proceedToMain() {
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //clear all activities from stack so user will not navigate to register/login when hitting 'back' button
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
