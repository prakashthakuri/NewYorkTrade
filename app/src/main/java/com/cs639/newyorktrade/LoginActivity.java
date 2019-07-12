package com.cs639.newyorktrade;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin;
    TextView btnSignup, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Set UI elements
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //Navigate to signup activity
        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
        //Navigate to password reset activity
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        //Authenticate and attempt sign in with given user credentials
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                //Verify fields are not empty

                if (TextUtils.isEmpty(email))
                    Toast.makeText(LoginActivity.this.getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(password))
                    Toast.makeText(LoginActivity.this.getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                //Show progressbar while authenticating user with server
                progressBar.setVisibility(View.VISIBLE);
                //Authenticate user
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    progressBar.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        //Notify user of login error
                        if (password.length() < 6)
                            inputPassword.setError(LoginActivity.this.getString(R.string.minimum_password));
                        else
                            Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    } else
//                        LoginActivity.this.
                        finish();
                });
            }
        });
    }


}