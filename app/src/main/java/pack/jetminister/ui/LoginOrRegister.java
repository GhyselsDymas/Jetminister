package pack.jetminister.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.EmailValidator;
import pack.jetminister.ui.util.PasswordValidator;
import pack.jetminister.ui.util.UsernameValidator;

public class LoginOrRegister extends AppCompatActivity {

    private static final String SHARED_PREFS = "SharedPreferences";
    private static final String SHARED_PREFS_USERNAME = "username";
    private static final String SHARED_PREFS_EMAIL = "email";
    private static final String SHARED_PREFS_DESCRIPTION = "description";
    private static final String SHARED_PREFS_THEME = "theme";
    private static final String SHARED_PREFS_IMAGE_URL = "imageURL";
    private static final String SHARED_PREFS_STREAMER = "streamer";


    //get an instance of Firebase and a reference to the collection
    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = rootNode.getReference("Users");
    private static final String TAG = "LoginOrRegister";
    private static final String BUNDLE_KEY_AUTHENTICATED_USER = "authenticated_user";
    private EditText usernameLoginET, passwordLoginET, usernameRegisterET, passwordRegisterET, confirmPasswordRegisterET, emailRegisterET;
    private Button loginBtn, registerBtn;
    private CheckBox checkboxLogin, checkboxRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        usernameLoginET = findViewById(R.id.edittext_login_username);
        passwordLoginET = findViewById(R.id.edittext_login_password);
        loginBtn = findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        emailRegisterET = findViewById(R.id.edittext_register_email);
        usernameRegisterET = findViewById(R.id.edittext_register_username);
        passwordRegisterET = findViewById(R.id.edittext_register_password);
        confirmPasswordRegisterET = findViewById(R.id.edittext_register_password_confirm);
        registerBtn = findViewById(R.id.btn_register);
        checkboxLogin = findViewById(R.id.action_checkbox_login);
        checkboxRegister = findViewById(R.id.action_checkbox_register);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get values from the text fields
                String newEmail = emailRegisterET.getText().toString().trim();
                String newUsername = usernameRegisterET.getText().toString().trim();
                String newPassword = passwordRegisterET.getText().toString();

                //check if the password fields match AND the email address is valid AND there is no duplicate username
                if (newPassword.equals(confirmPasswordRegisterET.getText().toString())
                        & EmailValidator.validate(newEmail) & !isDuplicate(dataSnapshot, newUsername)) {
                    //create new user with values from the textfields
                    User newUser = new User(newUsername, newPassword, newEmail);
                    //create new entry in database by username
                    // TODO: replace username with auto-generated id as child
                    usersRef.child(newUsername).setValue(newUser);
                    Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    authenticateUser(dataSnapshot, newUsername);

                } else {
                    Toast.makeText(LoginOrRegister.this, R.string.register_error, Toast.LENGTH_LONG).show();
                }
            }

                @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isDuplicate(DataSnapshot dataSnapshot, String newUsername){
        //loop over the database, convert each entry to User and get username to check for duplicates
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if (snapshot.exists()) {
                User checkUser = snapshot.getValue(User.class);
                if (checkUser != null) {
                    if (newUsername.equals(checkUser.getUsername())) {
                        Toast.makeText(LoginOrRegister.this, R.string.register_username_duplicate, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void loginUser() {
        final String inputUsername = usernameLoginET.getText().toString().trim();
        final String inputPassword = passwordLoginET.getText().toString();
        if (!UsernameValidator.validateUsername(inputUsername) || !PasswordValidator.validatePassword(inputPassword)){
            Toast.makeText(LoginOrRegister.this, R.string.login_fail, Toast.LENGTH_LONG).show();
        } else {
            Query checkUserQuery = usersRef.orderByChild("username").equalTo(inputUsername);
            checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    if (isRegisteredUser(userSnapshot)) {
                        if (isCorrectPassword(userSnapshot, inputUsername, inputPassword)) {
                            if(checkboxLogin.isChecked()){
                            saveUserInfo(userSnapshot, inputUsername);
                            }
                            authenticateUser(userSnapshot, inputUsername);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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


    private void authenticateUser(DataSnapshot snapshot, String username) {
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

    private boolean isRegisteredUser(DataSnapshot snapshot) {
        return snapshot.exists();
    }

    private boolean isCorrectPassword(DataSnapshot snapshot, String username, String password) {
        String passwordFromDatabase = snapshot.child(username).child("password").getValue(String.class);
        if (passwordFromDatabase != null) {
            return passwordFromDatabase.equals(password);
        }
        return false;
    }

}