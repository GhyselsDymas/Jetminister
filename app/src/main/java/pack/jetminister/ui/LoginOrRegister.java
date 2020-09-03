package pack.jetminister.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.EmailValidator;

public class LoginOrRegister extends AppCompatActivity {

    //get an instance of Firebase and a reference to the collection
    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = rootNode.getReference("Users");

    private EditText usernameLoginET, passwordLoginET, usernameRegisterET, passwordRegisterET, confirmPasswordRegisterET, emailRegisterET;
    private Button loginBtn, registerBtn;

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
                boolean isDuplicate = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        User checkUser = snapshot.getValue(User.class);
                        if (checkUser != null) {
                            if (newUsername.equals(checkUser.getUsername())) {
                                isDuplicate = true;
                                Toast.makeText(LoginOrRegister.this, R.string.register_username_duplicate, Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }
                if (newPassword.equals(confirmPasswordRegisterET.getText().toString())
                        & EmailValidator.validate(newEmail) & !isDuplicate) {
                    //TODO: replace username with auto-generated id
                    User newUser = new User(newUsername, newPassword, newEmail);
                    usersRef.child(newUsername).setValue(newUser);

                    Toast.makeText(LoginOrRegister.this, "Welcome to JetMinister", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginOrRegister.this, MainActivity.class);
                    startActivity(intent);
                }
            }

                @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//
//        //check if the password fields match AND the email address is valid
//
//            //create new user with values from the textfields
//            User newUser = new User(newUsername, newPassword, newEmail);
//
//            //create new entries in database by username
//                   } else {
//            Toast.makeText(LoginOrRegister.this, R.string.register_fail, Toast.LENGTH_LONG).show();
//        }
    }

    private void loginUser() {
        Toast.makeText(LoginOrRegister.this, R.string.login_fail, Toast.LENGTH_LONG).show();

    }


}