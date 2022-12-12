package com.example.truemoviefan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    EditText edUserName, edFullName, edUserEmail, edPassword;
    ImageView imAccount, imLogoUserProfile;
    Button btnEditUser, btnLogout;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialize();
    }

    private void initialize() {
        edUserName = findViewById(R.id.edUserName);
        edFullName = findViewById(R.id.edFullName);
        edUserEmail = findViewById(R.id.edUserEmail);
        edPassword = findViewById(R.id.edPassword);
        imAccount = findViewById(R.id.imAccount);
        imLogoUserProfile = findViewById(R.id.imLogoUserProfile);
        btnEditUser = findViewById(R.id.btnEditUser);
        btnLogout = findViewById(R.id.btnLogout);

        // Make EditText to be read-only
        edUserName.setEnabled(false);
        edFullName.setEnabled(false);
        edUserEmail.setEnabled(false);
        edPassword.setEnabled(false);

        // Firebase
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                edUserName.setText(value.getString("username"));
                edFullName.setText(value.getString("fullname"));
                edUserEmail.setText(value.getString("email"));
                edPassword.setText(value.getString("password"));

                //Fetching user's photo
                String img_url = value.getString("photo");
                Picasso.with(UserProfileActivity.this)
                        .load(img_url)
                        .into(imAccount);
            }
        });

        btnEditUser.setOnClickListener(view -> {
            Intent i = new Intent(this,EditUserProfileActivity.class);
            startActivity(i);
        });

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this,"Logged out Successfully!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        });

        imLogoUserProfile.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });
    }
}