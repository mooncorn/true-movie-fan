package com.example.truemoviefan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edUserNameSecondAct, edEmailSecondAct, edPasswordSecondAct, edConfirmPasswordSecondAct;
    Button btnSignUp, btnGobackToLogin;
    ImageView imLogoSecondAct;
    ProgressBar progressBarRegister;
    FirebaseAuth fAuth;
    String userId;
    FirebaseFirestore db;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        edUserNameSecondAct = findViewById(R.id.edUserNameSecondAct);
        edEmailSecondAct = findViewById(R.id.edEmailSecondAct);
        edPasswordSecondAct = findViewById(R.id.edPasswordSecondAct);
        edConfirmPasswordSecondAct = findViewById(R.id.edConfirmPasswordSecondAct);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGobackToLogin = findViewById(R.id.btnGobackToLogin);
        imLogoSecondAct = findViewById(R.id.imLogoSecondAct);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(view -> {
            registerNewUser();
        });

        btnGobackToLogin.setOnClickListener(view -> {
            gotoLoginPage();
        });

        imLogoSecondAct.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
        });
    }

    private void gotoLoginPage() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void registerNewUser() {
        String userName = edUserNameSecondAct.getText().toString().toString();
        String email = edEmailSecondAct.getText().toString().trim();
        String passw = edPasswordSecondAct.getText().toString().trim();
        String confirmPassw = edConfirmPasswordSecondAct.getText().toString().trim();

        if (TextUtils.isEmpty(userName)){
            edUserNameSecondAct.setError("Username is required!");
            return;
        }

        if (TextUtils.isEmpty(email)){
            edEmailSecondAct.setError("Email is required!");
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false){
            edEmailSecondAct.setError("Enter valid Email address!");
            return;
        }

        if(TextUtils.isEmpty(passw)){
            edPasswordSecondAct.setError("Password is required!");
            return;
        }

        if(passw.length() < 6){
            edPasswordSecondAct.setError("Password must not be less than 6 characters!");
            return;
        }

        if(TextUtils.isEmpty(confirmPassw)){
            edConfirmPasswordSecondAct.setError("Please, confirm your password!");
            return;
        }

        if(confirmPassw.equals(passw) == false){
            Toast.makeText(this, "Password does not match, please try again!",
                    Toast.LENGTH_LONG).show();
            edConfirmPasswordSecondAct.setText(null);
            edConfirmPasswordSecondAct.requestFocus();
            return;
        }

        progressBarRegister.setVisibility(View.VISIBLE);

        // Register a new user in Firebase
        fAuth.createUserWithEmailAndPassword(email,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // TODO: get information about current user
                    userId = fAuth.getCurrentUser().getUid();
                    documentReference = db.collection("user").document(userId);

                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("username",userName);
                    newUser.put("fullname", userName);
                    newUser.put("email", email);
                    newUser.put("password", passw);
                    newUser.put("photo", "https://www.nicepng.com/png/detail/18-181114_smiley-smile-smiley-faces-emojis-pb-logo-geocaching.png");

                    // Add a new document with the UUID that was generated with fAuth 
                    documentReference.set(newUser);

                    Toast.makeText(RegisterActivity.this,"User Created", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this,"Error! " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                    progressBarRegister.setVisibility(View.GONE);
                }
            }
        });
    }
}
