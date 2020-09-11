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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
        boolean checkTermsConditions = termsConditionCB.isChecked();
        authenticateRegisteredUser(newEmail, newUsername, newPassword, newConfirmPassword, checkTermsConditions);
    }

    private void authenticateRegisteredUser(final String email, final String username, final String password, String confirmPassword, boolean termsConditions) {
        if (validateEmail(email) && validatePassword(password) && confirmPasswordMatch(password, confirmPassword) && checkedTermsConditions(termsConditions)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User newUser = new User(email, password);
                                newUser.setUsername(username);
                                addUserToDatabase(newUser);
                                proceedToMain();
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                emailRegisterTIL.getEditText().requestFocus();
                                Toast.makeText(LoginOrRegister.this, R.string.register_error_email_duplicate, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, task.getException().getMessage());
                                emailRegisterTIL.getEditText().requestFocus();
                                Toast.makeText(LoginOrRegister.this, R.string.register_authentication_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, R.string.register_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserToDatabase(User user) {
        if (mAuth.getCurrentUser() != null){
            final String newUID = mAuth.getCurrentUser().getUid();
            usersRef.child(newUID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
//                        saveUserInfo(newUID);
                        Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d(TAG, task.getException().getMessage());
                    }
                }
            });
            Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();

        } else {
            Log.d(TAG, "mAuth.getCurrentUser() == null");
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

    private boolean checkedTermsConditions(boolean termsConditions){
        if (!termsConditions){
            Toast.makeText(this, R.string.register_error_termsconditions_unchecked, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        final String inputEmail = emailLoginTIL.getEditText().getText().toString().trim();
        final String inputPassword = passwordLoginTIL.getEditText().getText().toString();

        authenticateUserLogin(inputEmail, inputPassword);
    }

    private void authenticateUserLogin(String email, String password) {
        if (validateEmail(email) && validatePassword(password)){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            proceedToMain();
                            Toast.makeText(LoginOrRegister.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, task.getException().getMessage());

                            Toast.makeText(LoginOrRegister.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        emailLoginTIL.getEditText().requestFocus();
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

    private void proceedToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //clear all activities from stack so when user hits 'back' on phone, he will not return to loginactivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
