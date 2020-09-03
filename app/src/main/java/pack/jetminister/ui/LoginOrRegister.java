package pack.jetminister.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pack.jetminister.R;
import pack.jetminister.data.User;
import pack.jetminister.ui.util.EmailValidator;

public class LoginOrRegister extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

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
        EmailValidator emailValidator = new EmailValidator();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        //get values from the text fields
        String newEmail = emailRegisterET.getText().toString();
        String newUsername = usernameRegisterET.getText().toString();
        String newPassword = passwordRegisterET.getText().toString();
        if (newPassword.equals(confirmPasswordRegisterET.getText().toString())
                & EmailValidator.validate(newEmail)) {
            User newUser = new User(newUsername, newPassword, newEmail);
            usersRef.setValue(newUser);
            Toast.makeText(LoginOrRegister.this, R.string.register_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
        } else {
            Toast.makeText(LoginOrRegister.this, R.string.register_fail, Toast.LENGTH_LONG).show();
        }
    }

    private void loginUser() {
        Toast.makeText(LoginOrRegister.this, R.string.login_fail, Toast.LENGTH_LONG).show();

    }


}