package com.example.truemoviefan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ImageView imLogo;
    Button btnRegister, btnLogin;
    EditText edEmail, edUserPassword;
    ProgressBar progressBarLogin;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        imLogo = findViewById(R.id.imLogo);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        edEmail = findViewById(R.id.edEmail);
        edUserPassword = findViewById(R.id.edUserPassword);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        //Firebase
        fAuth = FirebaseAuth.getInstance();

        imLogo.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
        });

        btnRegister.setOnClickListener(view -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });

        btnLogin.setOnClickListener(view -> {
            userLogin();
        });


    }

    private void userLogin() {
        String email = edEmail.getText().toString().trim();
        String passw = edUserPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            edEmail.setError("Email is required!");
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false){
            edEmail.setError("Enter valid Email address!");
            return;
        }

        if(TextUtils.isEmpty(passw)){
            edUserPassword.setError("Password is required!");
            return;
        }

        if(passw.length() < 6){
            edUserPassword.setError("Password must not be less than 6 characters!");
            return;
        }

        progressBarLogin.setVisibility(View.VISIBLE);

        // Authenticate user logging in the application.
        fAuth.signInWithEmailAndPassword(email,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Logged in Successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this,"Error! " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                    progressBarLogin.setVisibility(View.GONE);
                }
            }
        });


    }

}